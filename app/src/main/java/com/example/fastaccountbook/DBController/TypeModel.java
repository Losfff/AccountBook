package com.example.fastaccountbook.DBController;

public class TypeModel {
    private int typeId;
    private String typeName;

    public TypeModel(){}

    public TypeModel(int typeId,String typeName){
        this.typeId=typeId;
        this.typeName=typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
