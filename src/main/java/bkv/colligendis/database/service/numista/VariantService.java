package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.features.Year;
import bkv.colligendis.database.entity.numista.CatalogueReference;
import bkv.colligendis.database.entity.numista.Mark;
import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.entity.numista.Variant;
import bkv.colligendis.rest.dto.NTypeVariantDTO;
import bkv.colligendis.services.AbstractService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VariantService extends AbstractService<Variant, VariantRepository> {

    private final ModelMapper modelMapper;

    public VariantService(VariantRepository repository, ModelMapper modelMapper) {
        super(repository);
        this.modelMapper = modelMapper;
    }

    // Start: Methods for Numista parsing

    public UUID findUuidByNid(String nid) {
        return findUuidByPropertyStringValue(Variant.LABEL, "nid", nid);
    }

    // Year

    public void setYear(UUID variantUuid, UUID yearUuid) {
        setSingleOutgoingRelationshipToNode(variantUuid, yearUuid, Variant.MILLESIME_AT, Year.LABEL);
    }

    public void detachYear(UUID variantUuid) {
        detachAllEntityWithRelationshipType(variantUuid, Variant.MILLESIME_AT);
    }

    public boolean compareYear(UUID variantUuid, UUID yearUuid) {
        return hasSingleRelationshipToNode(variantUuid, yearUuid, Variant.MILLESIME_AT);
    }

    // YearFrom

    public boolean compareYearFrom(UUID variantUuid, UUID yearUuid) {
        return hasSingleRelationshipToNode(variantUuid, yearUuid, Variant.MILLESIME_FROM);
    }

    public void setYearFrom(UUID variantUuid, UUID yearUuid) {
        setSingleOutgoingRelationshipToNode(variantUuid, yearUuid, Variant.MILLESIME_FROM, Year.LABEL);
    }

    // YearTill

    public boolean compareYearTill(UUID variantUuid, UUID yearUuid) {
        return hasSingleRelationshipToNode(variantUuid, yearUuid, Variant.MILLESIME_TILL);
    }

    public void setYearTill(UUID variantUuid, UUID yearUuid) {
        setSingleOutgoingRelationshipToNode(variantUuid, yearUuid, Variant.MILLESIME_TILL, Year.LABEL);
    }

    // MintLetter
    public boolean compareMintLetter(UUID variantUuid, String mintLetter) {
        return comparePropertyValue(variantUuid, "mintLetter", mintLetter, String.class);
    }

    public void setMintLetter(UUID variantUuid, String mintLetter) {
        setPropertyStringValue(variantUuid, "mintLetter", mintLetter);
    }

    // Marks

    public boolean equateMarks(UUID variantUuid, List<UUID> marksUUIDs) {
        return equateFistListToSecondList(getMarks(variantUuid), marksUUIDs, this::detachMark, this::addMark,
                variantUuid);
    }

    public List<UUID> getMarks(UUID variantUuid) {
        return getAllOutgoingRelatedNodesUUIDs(variantUuid, Variant.WITH_MARK, Mark.LABEL);
    }

    public boolean detachMark(UUID variantUuid, UUID markUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(variantUuid, markUuid, Variant.WITH_MARK);
        return true;
    }

    public boolean addMark(UUID variantUuid, UUID markUuid) {
        addSingleOutgoingRelationshipToNode(variantUuid, markUuid, Variant.WITH_MARK);
        return true;
    }

    // Mintage

    public boolean compareMintage(UUID variantUuid, int mintage) {
        return comparePropertyValue(variantUuid, "mintage", mintage, Integer.class);
    }

    public void setMintage(UUID variantUuid, int mintage) {
        setPropertyIntValue(variantUuid, "mintage", mintage);
    }

    // CatalogueReferences
    public boolean equateCatalogueReferences(UUID variantUuid, List<UUID> catalogueReferencesUUIDs) {
        return equateFistListToSecondList(getCatalogueReferences(variantUuid), catalogueReferencesUUIDs,
                this::detachCatalogueReference, this::addCatalogueReference, variantUuid);
    }

    public List<UUID> getCatalogueReferences(UUID variantUuid) {
        return getAllOutgoingRelatedNodesUUIDs(variantUuid, NType.HAS_REFERENCE, CatalogueReference.LABEL);
    }

    public boolean detachCatalogueReference(UUID variantUuid, UUID catalogueReferenceUuid) {
        detachEntityFromAnotherEntityWithRelationshipType(variantUuid, catalogueReferenceUuid, NType.HAS_REFERENCE);
        return true;
    }

    public boolean addCatalogueReference(UUID variantUuid, UUID catalogueReferenceUuid) {
        addSingleOutgoingRelationshipToNode(variantUuid, catalogueReferenceUuid, NType.HAS_REFERENCE);
        return true;
    }

    // Comment

    public boolean compareComment(UUID variantUuid, String comment) {
        return comparePropertyValue(variantUuid, "comment", comment, String.class);
    }

    public void setComment(UUID variantUuid, String comment) {
        setPropertyStringValue(variantUuid, "comment", comment);
    }
    // End: Methods for Numista parsing

    public Variant findByNid(String nid) {
        Variant mark = repository.findByNid(nid);
        if (mark == null) {
            return repository.save(new Variant(nid));
        }
        return mark;
    }

    public List<Integer> getYearsOfVariantsByIssuerEid(String eid) {

        List<Integer> gregorianYears = repository.getYearsOfVariantsByIssuerEid(eid);
        Set<Integer> years = new HashSet<>(Objects.requireNonNull(gregorianYears));

        List<Variant> variantFlux = repository.getBetweenMinMaxYearsOfVariantsByIssuerEid(eid);

        variantFlux.forEach(variant -> {
            if (variant.getMinYear() != null && variant.getMaxYear() != null) {
                if (variant.getMinYear() != Integer.MIN_VALUE && variant.getMaxYear() != Integer.MAX_VALUE) {
                    for (int i = variant.getMinYear(); i <= variant.getMaxYear(); i++) {
                        years.add(i);
                    }
                }
            }
        });
        return years.stream().sorted(Integer::compareTo).collect(Collectors.toList());
    }

    public List<NTypeVariantDTO> getVariantsByNTypeNid(String nTypeNid) {
        List<Variant> variants = repository.getVariantsByNTypeNid(nTypeNid);
        return variants.stream().map(variant -> modelMapper.map(variant, NTypeVariantDTO.class))
                .collect(Collectors.toList());
    }

    // Statistics

    public Integer countVariantsByCountryNumistaCodeAndCollectibleTypeCode(String numistaCode,
            String collectibleTypeCode) {
        return repository.countVariantsByCountryNumistaCodeAndCollectibleTypeCode(numistaCode, collectibleTypeCode);
    }

    public Integer countVariantsBySubjectNumistaCodeAndCollectibleTypeCode(String numistaCode,
            String collectibleTypeCode) {
        return repository.countVariantsBySubjectNumistaCodeAndCollectibleTypeCode(numistaCode, collectibleTypeCode);
    }

    public Integer countVariantsByIssuerCodeAndCollectibleTypeCode(String code, String collectibleTypeCode) {
        return repository.countVariantsByIssuerCodeAndCollectibleTypeCode(code, collectibleTypeCode);
    }

}
