package bkv.colligendis.services;

import bkv.colligendis.database.service.meshok.MeshokCategoryService;
import bkv.colligendis.database.service.meshok.MeshokLotService;
import bkv.colligendis.database.service.meshok.MeshokSellerService;

import org.springframework.stereotype.Service;

@Service
public class MeshokServices {

    public final MeshokCategoryService meshokCategoryService;
    public final MeshokLotService meshokLotService;
    public final MeshokSellerService meshokSellerService;

    public MeshokServices(MeshokCategoryService meshokCategoryService, MeshokLotService meshokLotService,
            MeshokSellerService meshokSellerService) {
        this.meshokCategoryService = meshokCategoryService;
        this.meshokLotService = meshokLotService;
        this.meshokSellerService = meshokSellerService;
    }

}
