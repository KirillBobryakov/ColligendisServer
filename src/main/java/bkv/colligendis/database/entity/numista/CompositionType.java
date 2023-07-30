package bkv.colligendis.database.entity.numista;

public enum CompositionType {

    plain("Single material"),
    plated("Plated metal"),
    clad("Clad metal"),
    bimetallic("Bimetallic"),
    bimetallic_plated("Bimetallic with plated metal centre"),
    bimetallic_plated_ring("Bimetallic with plated metal ring"),
    bimetallic_plated_plated("Bimetallic with plated centre and ring"),
    bimetallic_clad("Bimetallic with clad metal centre"),
    trimetallic("Trimetallic"),
    other("Other");

    public final String composition;

    private CompositionType(String composition) {
        this.composition = composition;
    }
}
