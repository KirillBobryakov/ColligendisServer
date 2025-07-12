package bkv.colligendis.database.entity.numista;

public enum GradeType {
    G("ab"),
    VG("b"),
    F("tb"),
    VF("ttb"),
    XF("sup"),
    AU("spl"),
    UNC("fdc");

    private final String value;

    GradeType(String value) {
        this.value = value;
    }

    public static GradeType fromValue(String value) {
        for (GradeType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown GradeType value: " + value);
    }

    public String getNumistaValue() {
        return value;
    }

    @Override
    public String toString() {
        return name();
    }
}