package com.smart.shop.service;

import com.smart.shop.entity.Client;
import com.smart.shop.enums.CustomerTier;
import com.smart.shop.mapper.ClientMapper;
import com.smart.shop.mapper.CommandeMapper;
import com.smart.shop.repository.ClientRepository;
import com.smart.shop.repository.CommandeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private CommandeMapper commandeMapper;

    @InjectMocks
    private ClientService clientService;
    
    @Captor
    private ArgumentCaptor<Client> clientArgumentCaptor;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId("client-123");
        client.setNom("Test Client");
        client.setEmail("test@example.com");
    }

    private void testAndVerifyTier(long numOrders, String totalSpent, CustomerTier expectedTier, LocalDateTime firstOrder, LocalDateTime lastOrder) {
        // Given
        Object[] stats = new Object[]{numOrders, new BigDecimal(totalSpent), firstOrder, lastOrder};
        Object[] repoResponse = new Object[] {stats};

        when(clientRepository.findById("client-123")).thenReturn(Optional.of(client));
        when(commandeRepository.getClientStats("client-123")).thenReturn(repoResponse);

        // When
        clientService.mettreAJourStatistiquesClient("client-123");

        // Then
        verify(clientRepository).save(clientArgumentCaptor.capture());
        Client savedClient = clientArgumentCaptor.getValue();

        assertEquals(expectedTier, savedClient.getNiveauFidelite());
        assertEquals(numOrders, savedClient.getNombreCommandes());
        assertEquals(new BigDecimal(totalSpent), savedClient.getMontantTotalDepense());
        assertEquals(firstOrder, savedClient.getDatePremiereCommande());
        assertEquals(lastOrder, savedClient.getDateDerniereCommande());
    }

    @Test
    void testMettreAJourStatistiques_NiveauBasic() {
        testAndVerifyTier(2, "999.99", CustomerTier.BASIC, null, null);
    }

    @Test
    void testMettreAJourStatistiques_NiveauSilverParCommandes() {
        testAndVerifyTier(3, "500", CustomerTier.SILVER, null, null);
    }

    @Test
    void testMettreAJourStatistiques_NiveauSilverParMontant() {
        testAndVerifyTier(2, "1000", CustomerTier.SILVER, null, null);
    }

    @Test
    void testMettreAJourStatistiques_NiveauGoldParCommandes() {
        testAndVerifyTier(10, "4000", CustomerTier.GOLD, null, null);
    }

    @Test
    void testMettreAJourStatistiques_NiveauGoldParMontant() {
        testAndVerifyTier(9, "5000", CustomerTier.GOLD, null, null);
    }

    @Test
    void testMettreAJourStatistiques_NiveauPlatinumParCommandes() {
        testAndVerifyTier(20, "14000", CustomerTier.PLATINUM, null, null);
    }

    @Test
    void testMettreAJourStatistiques_NiveauPlatinumParMontant() {
        testAndVerifyTier(19, "15000", CustomerTier.PLATINUM, null, null);
    }

    @Test
    void testMettreAJourStatistiques_AvecDates() {
        LocalDateTime first = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime last = LocalDateTime.of(2023, 1, 31, 10, 0);
        testAndVerifyTier(1, "100.00", CustomerTier.BASIC, first, last);
    }
}
