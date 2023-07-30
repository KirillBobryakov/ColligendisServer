package bkv.colligendis.database.entity.item;


import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.core.schema.Node;

@Node("VARIANT_DIFFERENCE")
public class VariantDifference extends AbstractEntity {

    private String photoLink;
    private String description;

    public VariantDifference() {
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
