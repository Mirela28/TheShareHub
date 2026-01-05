package com.thesharehub.TheShareHub.mapper;

import com.thesharehub.TheShareHub.dtos.ItemDTO;
import com.thesharehub.TheShareHub.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface ItemDtoMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "image", expression = "java(fromBase64(itemDTO.getImage()))")
    Item toDomain(ItemDTO itemDTO);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(source = "owner.name", target = "ownerName")
    @Mapping(source = "owner.phone", target = "ownerPhone")
    @Mapping(source = "owner.email", target = "ownerEmail")
    @Mapping(target = "image", expression = "java(toBase64(item.getImage()))")
    ItemDTO toDTO(Item item);

    default String toBase64(byte[] imageBytes) {
        return (imageBytes != null && imageBytes.length > 0)
                ? Base64.getEncoder().encodeToString(imageBytes)
                : null;
    }

    default byte[] fromBase64(String base64String) {
        return (base64String != null && !base64String.isEmpty())
                ? Base64.getDecoder().decode(base64String)
                : null;
    }
}
