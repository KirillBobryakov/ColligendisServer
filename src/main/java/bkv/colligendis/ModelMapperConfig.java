package bkv.colligendis;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.entity.numista.Denomination;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Item;
import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.database.entity.numista.Variant;
import bkv.colligendis.rest.catalogue.csi_statistics.CSITreeNode;
import bkv.colligendis.rest.dto.CurrencyDTO;
import bkv.colligendis.rest.dto.DenominationDTO;
import bkv.colligendis.rest.dto.IssuerDTO;
import bkv.colligendis.rest.dto.ItemDAO;
import bkv.colligendis.rest.dto.NTypeDTO;
import bkv.colligendis.rest.dto.NTypeVariantDTO;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT);

        modelMapper.typeMap(Country.class, CSITreeNode.class)
                .addMappings(mapper -> {
                    mapper.map(Country::getName, CSITreeNode::setName);
                    mapper.map(Country::getNumistaCode, CSITreeNode::setCode);
                    mapper.map(Country::getRuAlternativeNames, CSITreeNode::setRuAlternativeNames);
                });
        modelMapper.typeMap(Subject.class, CSITreeNode.class)
                .addMappings(mapper -> {
                    mapper.map(Subject::getName, CSITreeNode::setName);
                    mapper.map(Subject::getNumistaCode, CSITreeNode::setCode);
                    mapper.map(Subject::getRuAlternativeNames, CSITreeNode::setRuAlternativeNames);
                });
        modelMapper.typeMap(Issuer.class, CSITreeNode.class)
                .addMappings(mapper -> {
                    mapper.map(Issuer::getName, CSITreeNode::setName);
                    mapper.map(Issuer::getCode, CSITreeNode::setCode);
                    mapper.map(Issuer::getRuAlternativeNames, CSITreeNode::setRuAlternativeNames);
                });
        modelMapper.typeMap(Currency.class, CurrencyDTO.class)
                .addMappings(mapper -> {
                    mapper.map(Currency::getName, CurrencyDTO::setName);
                    mapper.map(Currency::getNid, CurrencyDTO::setNid);
                    mapper.map(Currency::getFullName, CurrencyDTO::setFullName);
                });
        modelMapper.typeMap(Variant.class, NTypeVariantDTO.class)
                .addMappings(mapper -> {
                    mapper.map(Variant::getNid, NTypeVariantDTO::setNid);
                    mapper.map(Variant::isDated, NTypeVariantDTO::setIsDated);
                    mapper.map(Variant::getYear, NTypeVariantDTO::setYear);
                    mapper.map(Variant::getYearFrom, NTypeVariantDTO::setYearFrom);
                    mapper.map(Variant::getYearTill, NTypeVariantDTO::setYearTill);
                    mapper.map(Variant::getMintage, NTypeVariantDTO::setMintage);
                    mapper.map(Variant::getComment, NTypeVariantDTO::setComment);
                });
        modelMapper.typeMap(Issuer.class, IssuerDTO.class)
                .addMappings(mapper -> {
                    mapper.map(Issuer::getCode, IssuerDTO::setCode);
                    mapper.map(Issuer::getName, IssuerDTO::setName);
                    mapper.map(Issuer::getRuAlternativeNames, IssuerDTO::setRuAlternativeNames);
                });
        modelMapper.typeMap(Denomination.class, DenominationDTO.class)
                .addMappings(mapper -> {
                    mapper.map(Denomination::getNid, DenominationDTO::setNid);
                    mapper.map(Denomination::getFullName, DenominationDTO::setFullName);
                    mapper.map(Denomination::getName, DenominationDTO::setName);
                    mapper.map(Denomination::getNumericValue, DenominationDTO::setNumericValue);
                });
        modelMapper.createTypeMap(NType.class, NTypeDTO.class)
                .addMappings(mapper -> {
                    mapper.map(NType::getNid, NTypeDTO::setNid);
                    mapper.map(NType::getTitle, NTypeDTO::setTitle);
                    mapper.map(NType::getCollectibleType, NTypeDTO::setCollectibleType);
                    mapper.map(NType::getVariants, NTypeDTO::setVariants);
                });
        modelMapper.createTypeMap(Item.class, ItemDAO.class)
                .addMappings(mapper -> {
                    mapper.map(Item::getUuid, ItemDAO::setUuid);
                    mapper.map(Item::getCreatedAt, ItemDAO::setCreatedAt);
                    mapper.map(Item::getQuantity, ItemDAO::setQuantity);
                    mapper.map(Item::getGradeType, ItemDAO::setGradeType);
                    mapper.map(Item::getPriceValue, ItemDAO::setPriceValue);
                    mapper.map(Item::getPriceCurrency, ItemDAO::setPriceCurrency);
                    mapper.map(Item::getAcquisitionDate, ItemDAO::setAcquisitionDate);
                    mapper.map(source -> source.getVariant().getNid(), ItemDAO::setVariantNid);
                    mapper.map(source -> source.getUser().getUuid(), ItemDAO::setUserUuid);
                });
        modelMapper.createTypeMap(ItemDAO.class, Item.class)
                .addMappings(mapper -> {
                    mapper.map(ItemDAO::getUuid, Item::setUuid);
                    mapper.map(ItemDAO::getCreatedAt, Item::setCreatedAt);
                    mapper.map(ItemDAO::getQuantity, Item::setQuantity);
                    mapper.map(ItemDAO::getGradeType, Item::setGradeType);
                    mapper.map(ItemDAO::getPriceValue, Item::setPriceValue);
                    mapper.map(ItemDAO::getPriceCurrency, Item::setPriceCurrency);
                    mapper.map(ItemDAO::getAcquisitionDate, Item::setAcquisitionDate);
                    mapper.skip(Item::setVariant);
                    mapper.skip(Item::setUser);
                });

        return modelMapper;
    }

}
