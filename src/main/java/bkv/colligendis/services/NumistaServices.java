package bkv.colligendis.services;


import bkv.colligendis.database.service.numista.*;
import bkv.colligendis.database.service.features.YearService;
import org.springframework.stereotype.Service;

@Service
public class NumistaServices {


    public final CountryService countryService;

    public final SubjectService subjectService;

    public final NTypeService nTypeService;
    public final CategoryService categoryService;
    public final IssuerService issuerService;
    public final RulerService rulerService;
    public final RulerGroupService rulerGroupService;
    public final IssuingEntityService issuingEntityService;

    public final DenominationService denominationService;
    public final CurrencyService currencyService;
    public final CollectibleTypeService collectibleTypeService;
    public final TypeGroupService typeGroupService;

    public final CommemoratedEventService commemoratedEventService;
    public final SeriesService seriesService;
    public final CatalogueService catalogueService;
    public final CatalogueReferenceService catalogueReferenceService;

    public final CompositionService compositionService;
    public final MetalService metalService;
    public final ShapeService shapeService;

    public final TechniqueService techniqueService;


    public final NTypePartService nTypePartService;


    public final LetteringScriptService letteringScriptService;
    public final MintService mintService;
    public final MintmarkService mintmarkService;
    public final SpecifiedMintService specifiedMintService;
    public final PrinterService printerService;

    public final NTagService nTagService;

    public final CalendarService calendarService;
    public final YearService yearService;
    public final VariantService variantService;

    public final MarkService markService;





    public NumistaServices(CountryService countryService, SubjectService subjectService, NTypeService nTypeService, CategoryService categoryService, IssuerService issuerService, RulerService rulerService, RulerGroupService rulerGroupService, IssuingEntityService issuingEntityService, DenominationService denominationService, CurrencyService currencyService, CollectibleTypeService collectibleTypeService, TypeGroupService typeGroupService, CommemoratedEventService commemoratedEventService, SeriesService seriesService, CatalogueService catalogueService, CatalogueReferenceService catalogueReferenceService, CompositionService compositionService, MetalService metalService, ShapeService shapeService, TechniqueService techniqueService, NTypePartService nTypePartService, LetteringScriptService letteringScriptService, MintService mintService, MintmarkService mintmarkService, SpecifiedMintService specifiedMintService, PrinterService printerService, NTagService nTagService, CalendarService calendarService, YearService yearService, VariantService variantService, MarkService markService) {
        this.countryService = countryService;
        this.subjectService = subjectService;
        this.nTypeService = nTypeService;
        this.categoryService = categoryService;
        this.issuerService = issuerService;
        this.rulerService = rulerService;
        this.rulerGroupService = rulerGroupService;
        this.issuingEntityService = issuingEntityService;
        this.denominationService = denominationService;
        this.currencyService = currencyService;
        this.collectibleTypeService = collectibleTypeService;
        this.typeGroupService = typeGroupService;
        this.commemoratedEventService = commemoratedEventService;
        this.seriesService = seriesService;
        this.catalogueService = catalogueService;
        this.catalogueReferenceService = catalogueReferenceService;
        this.compositionService = compositionService;
        this.metalService = metalService;
        this.shapeService = shapeService;
        this.techniqueService = techniqueService;
        this.nTypePartService = nTypePartService;
        this.letteringScriptService = letteringScriptService;
        this.mintService = mintService;
        this.mintmarkService = mintmarkService;
        this.specifiedMintService = specifiedMintService;
        this.printerService = printerService;
        this.nTagService = nTagService;
        this.calendarService = calendarService;
        this.yearService = yearService;
        this.variantService = variantService;
        this.markService = markService;
    }


}
