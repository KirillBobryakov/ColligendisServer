package bkv.colligendis.services;

import bkv.colligendis.database.service.numista.*;
import bkv.colligendis.services.features.*;
import org.springframework.stereotype.Service;

@Service
public class ServiceUtils {

    public final TerritoryService territoryService;
    public final ItemSideService itemSideService;
    public final CommIssueSeriesService commIssueSeriesService;
    public final ItemService itemService;
    public final IssuerService issuerService;
    public final PeriodService periodService;
    public final ValueService valueService;
    public final CurrencyService currencyService;
    public final MintService mintService;
    public final MarkService markService;
    public final VariantService variantService;
    public final VariantDifferenceService variantDifferenceService;



    public ServiceUtils(TerritoryService territoryService,
                        ItemSideService itemSideService,
                        CommIssueSeriesService commIssueSeriesService,
                        ItemService itemService,
                        IssuerService issuerService,
                        PeriodService periodService,
                        ValueService valueService,
                        CurrencyService currencyService,
                        MintService mintService, MarkService markService, VariantService variantService, VariantDifferenceService variantDifferenceService) {
        this.territoryService = territoryService;
        this.itemSideService = itemSideService;
        this.commIssueSeriesService = commIssueSeriesService;
        this.itemService = itemService;
        this.issuerService = issuerService;
        this.periodService = periodService;
        this.valueService = valueService;
        this.currencyService = currencyService;
        this.mintService = mintService;
        this.markService = markService;
        this.variantService = variantService;
        this.variantDifferenceService = variantDifferenceService;
    }

    public void setValue(Long nodeId, String name, String value) {


    }


}
