    package com.smart.shop.mapper;

    import com.smart.shop.dto.OrderItem.OrderItemCreateDto;
    import com.smart.shop.dto.OrderItem.OrderItemResponseDto;
    import com.smart.shop.entity.OrderItem;
    import com.smart.shop.entity.Product;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;
    import org.mapstruct.Named;
    import org.mapstruct.factory.Mappers;

    @Mapper(componentModel = "spring")
    public interface OrderItemMapper {
        OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "commande", ignore = true)
        @Mapping(target = "produit", source = "produitId", qualifiedByName = "idToProduit")
        OrderItem toEntity(OrderItemCreateDto dto);

        @Mapping(target = "produitId", source = "produit.id")
        @Mapping(target = "nomProduit", source = "produit.nom")
        OrderItemResponseDto toDto(OrderItem entity);

        @Named("idToProduit")
        default Product idToProduit(String produitId) {
            if (produitId == null) {
                return null;
            }
            Product produit = new Product();
            produit.setId(produitId);
            return produit;
        }
    }
