package com.smart.shop.service;

import com.smart.shop.dto.Paiement.PaiementCreateDto;
import com.smart.shop.dto.Paiement.PaiementStatutUpdateDto;
import com.smart.shop.dto.Paiement.PaiementResponseDto;
import com.smart.shop.entity.Commande;
import com.smart.shop.entity.Paiement;
import com.smart.shop.enums.PaymentStatus;
import com.smart.shop.enums.TypePaiement;
import com.smart.shop.exception.ResourceNotFoundException;
import com.smart.shop.mapper.PaiementMapper;
import com.smart.shop.repository.CommandeRepository;
import com.smart.shop.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final CommandeRepository commandeRepository;
    private final PaiementMapper paiementMapper;

    // -----------------------------------------------------------
    //                   CRÉATION D’UN PAIEMENT
    // -----------------------------------------------------------
    @Transactional
    public PaiementResponseDto creerPaiement(PaiementCreateDto dto) {

        // 1) Récupérer la commande
        Commande commande = commandeRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée avec l'ID : " + dto.getCommandeId()
                ));

        // 2) Validation du montant
        if (dto.getMontant().compareTo(commande.getMontantRestant()) > 0) {
            throw new IllegalStateException(
                    "Le montant du paiement (" + dto.getMontant() +
                            ") dépasse le montant restant (" + commande.getMontantRestant() + ")"
            );
        }

        // 3) Mapper DTO → entité
        Paiement paiement = paiementMapper.toEntity(dto);
        paiement.setCommande(commande);
        paiement.setDatePaiement(LocalDateTime.now());

        // 4) Générer numéro séquentiel du paiement
        paiement.setNumeroPaiement(genererNumeroPaiement(commande.getId()));

        // 5) Gestion des statuts + contrôle des champs selon type
        appliquerLogiqueSelonTypePaiement(dto, paiement);

        // 6) Mise à jour du montant restant de la commande
        commande.setMontantRestant(
                commande.getMontantRestant().subtract(dto.getMontant())
        );

        // 7) Sauvegarde
        Paiement saved = paiementRepository.save(paiement);
        commandeRepository.save(commande);

        return paiementMapper.toDto(saved);
    }

    // -----------------------------------------------------------
    //           MISE À JOUR DU STATUT D’UN PAIEMENT
    // -----------------------------------------------------------
    @Transactional
    public PaiementResponseDto mettreAJourStatutPaiement(String paiementId, PaiementStatutUpdateDto dto) {

        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paiement non trouvé avec l'ID : " + paiementId
                ));

        Commande commande = paiement.getCommande();

        PaymentStatus ancien = paiement.getStatut();
        PaymentStatus nouveau = dto.getStatut();

        if (ancien == nouveau) {
            return paiementMapper.toDto(paiement);
        }

        switch (nouveau) {

            case ENCAISSÉ:
                // Si un paiement passe à ENCAISSÉ pour la première fois
                if (ancien == PaymentStatus.EN_ATTENTE || ancien == PaymentStatus.REJETÉ) {
                    commande.setMontantRestant(
                            commande.getMontantRestant().subtract(paiement.getMontant())
                    );
                }
                paiement.setDateEncaissement(LocalDateTime.now());
                break;


            case REJETÉ:
                // Si EN_ATTENTE ou ENCAISSÉ → REJETÉ → on réajoute le montant
                if (ancien == PaymentStatus.EN_ATTENTE || ancien == PaymentStatus.ENCAISSÉ) {
                    commande.setMontantRestant(
                            commande.getMontantRestant().add(paiement.getMontant())
                    );
                }
                paiement.setDateEncaissement(null);
                break;
        }

        paiement.setStatut(nouveau);

        paiementRepository.save(paiement);
        commandeRepository.save(commande);

        return paiementMapper.toDto(paiement);
    }

    // -----------------------------------------------------------
    //                 GÉNÉRATION DU NUMÉRO SÉQUENTIEL
    // -----------------------------------------------------------
    private int genererNumeroPaiement(String commandeId) {
        int last = paiementRepository.findLastPaymentNumber(commandeId);
        return last + 1;
    }

    // -----------------------------------------------------------
    //           LOGIQUE SPÉCIFIQUE PAR TYPE DE PAIEMENT
    // -----------------------------------------------------------
    private void appliquerLogiqueSelonTypePaiement(PaiementCreateDto dto, Paiement paiement) {

        TypePaiement type = dto.getTypePaiement();

        switch (type) {

            case ESPECES:
                paiement.setStatut(PaymentStatus.ENCAISSÉ);
                paiement.setDateEncaissement(LocalDateTime.now());
                break;

            case CHEQUE:
                if (dto.getNumeroCheque() == null || dto.getNumeroCheque().isBlank())
                    throw new IllegalArgumentException("Le numéro du chèque est obligatoire.");

                if (dto.getNomBanque() == null || dto.getNomBanque().isBlank())
                    throw new IllegalArgumentException("Le nom de la banque est obligatoire.");

                if (dto.getDateEcheance() == null)
                    throw new IllegalArgumentException("La date d'échéance du chèque est obligatoire.");

                paiement.setStatut(PaymentStatus.EN_ATTENTE);
                paiement.setNumeroCheque(dto.getNumeroCheque());
                paiement.setNomBanque(dto.getNomBanque());
                paiement.setDateEcheance(dto.getDateEcheance());
                break;

            case VIREMENT:
                if (dto.getReferenceVirement() == null || dto.getReferenceVirement().isBlank())
                    throw new IllegalArgumentException("La référence de virement est obligatoire.");

                paiement.setStatut(PaymentStatus.EN_ATTENTE);
                paiement.setReferenceVirement(dto.getReferenceVirement());
                break;
        }
    }
}
