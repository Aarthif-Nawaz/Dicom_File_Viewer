package com.athif_innovatives.myapplication;

class Dicom_Extractor {
    private String tagId;
    private String tagName;
    private String Description;

    public Dicom_Extractor(String tagId, String tagName, String description) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.Description = description;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
