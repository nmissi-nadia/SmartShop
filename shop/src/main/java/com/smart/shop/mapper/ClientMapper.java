package com.smart.shop.mapper;

import com.smart.shop.dto.ClientDto;
import com.smart.shop.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    ClientDto toClientDto(Client client);

    Client toClient(ClientDto clientDto);
}
