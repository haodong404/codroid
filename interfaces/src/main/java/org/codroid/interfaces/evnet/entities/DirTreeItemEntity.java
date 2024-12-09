package org.codroid.interfaces.evnet.entities;

import org.codroid.interfaces.env.ImageResource;

public class DirTreeItemEntity {
    private String title;
    private ImageResource icon;
    private ImageResource tagIcon;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ImageResource getIcon() {
        return icon;
    }

    public void setIcon(ImageResource icon) {
        this.icon = icon;
    }

    public ImageResource getTagIcon() {
        return tagIcon;
    }

    public void setTagIcon(ImageResource tagIcon) {
        this.tagIcon = tagIcon;
    }
}
