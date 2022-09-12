package bkv.colligendis.services;

import bkv.colligendis.services.features.*;
import org.springframework.stereotype.Service;

@Service
public class ServiceUtils {

    public final TerritoryService territoryService;
    public final CoinPartService coinPartService;
    public final CommIssueSeriesService commIssueSeriesService;
    public final CoinInformationService coinInformationService;
    public final IssuerService issuerService;
    public final PeriodService periodService;
    public final ValueService valueService;
    public final CurrencyService currencyService;
    public final MintService mintService;


    public ServiceUtils(TerritoryService territoryService,
                        CoinPartService coinPartService,
                        CommIssueSeriesService commIssueSeriesService,
                        CoinInformationService coinInformationService,
                        IssuerService issuerService,
                        PeriodService periodService,
                        ValueService valueService,
                        CurrencyService currencyService,
                        MintService mintService) {
        this.territoryService = territoryService;
        this.coinPartService = coinPartService;
        this.commIssueSeriesService = commIssueSeriesService;
        this.coinInformationService = coinInformationService;
        this.issuerService = issuerService;
        this.periodService = periodService;
        this.valueService = valueService;
        this.currencyService = currencyService;
        this.mintService = mintService;
    }
}
