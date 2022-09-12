package bkv.colligendis.rest;


import bkv.colligendis.database.entity.piece.CoinInformation;
import bkv.colligendis.database.entity.piece.PieceInformation;
import bkv.colligendis.database.entity.piece.PieceSide;
import bkv.colligendis.services.features.CoinInformationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
public class PieceService {



    private final CoinInformationService coinInformationService;

    public PieceService(CoinInformationService coinInformationService) {
        this.coinInformationService = coinInformationService;
    }

//    @GetMapping(value = "/coininfo/{id}")
//    public ResponseEntity<List<PieceSide>> read(@PathVariable(name = "id") int id) {
//        final CoinInformation coinInformation = coinInformationService.findByNumistaNumber(String.valueOf(id));
//        List<PieceSide> pieceSides = new ArrayList<>();
//        pieceSides.add(coinInformation.getObverse());
////        return coinInformation != null
////                ? new ResponseEntity<>(coinInformation.getObverse(), HttpStatus.OK)
////                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        return new ResponseEntity<>(pieceSides, HttpStatus.OK);
//    }

    @GetMapping(value = "/coininfo/{id}")
    public ResponseEntity<List<CoinInformation>> readCI(@PathVariable(name = "id") int id){
        final CoinInformation coinInformation = coinInformationService.findByNumistaNumber(String.valueOf(id));
        return new ResponseEntity<>(Collections.singletonList(coinInformation), HttpStatus.OK);
    }




}
