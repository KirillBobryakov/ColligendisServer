package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.database.entity.numista.Mintmark;
import bkv.colligendis.database.entity.numista.SpecifiedMint;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class SpecifiedMintService extends AbstractService<SpecifiedMint, SpecifiedMintRepository> {
    public SpecifiedMintService(SpecifiedMintRepository repository) {
        super(repository);
    }

    // Start: Methods for Numista parsing

    public UUID findUuidByIdentifierMintMintmark(String identifier, UUID mintUuid, UUID mintmarkUuid) {
        String specifiedMintUuid = repository.findUuidByIdentifierMintMintmark(identifier, mintUuid.toString(),
                mintmarkUuid.toString());
        return specifiedMintUuid != null ? UUID.fromString(specifiedMintUuid) : null;
    }

    // Mint

    public boolean compareMint(UUID specifiedMintUuid, UUID mintUuid) {
        return hasSingleRelationshipToNode(specifiedMintUuid, mintUuid, SpecifiedMint.WITH_MINT);
    }

    public void setMint(UUID specifiedMintUuid, UUID mintUuid) {
        setSingleOutgoingRelationshipToNode(specifiedMintUuid, mintUuid, SpecifiedMint.WITH_MINT, Mint.LABEL);
    }

    // Mintmark

    public boolean compareMintmark(UUID specifiedMintUuid, UUID mintmarkUuid) {
        return hasSingleRelationshipToNode(specifiedMintUuid, mintmarkUuid, SpecifiedMint.WITH_MINTMARK);
    }

    public void setMintmark(UUID specifiedMintUuid, UUID mintmarkUuid) {
        setSingleOutgoingRelationshipToNode(specifiedMintUuid, mintmarkUuid, SpecifiedMint.WITH_MINTMARK,
                Mintmark.LABEL);
    }

    // End: Methods for Numista parsing

    public SpecifiedMint findByIdentifierMintMintmark(String identifier, String mintNid, String mintmarkNid) {
        SpecifiedMint specifiedMint = null;
        if (mintmarkNid == null) {
            specifiedMint = repository.findByIdentifierMintWithoutMintmark(identifier, mintNid);
        } else {
            specifiedMint = repository.findByIdentifierMintMintmark(identifier, mintNid, mintmarkNid);
        }

        return specifiedMint;
    }

}
