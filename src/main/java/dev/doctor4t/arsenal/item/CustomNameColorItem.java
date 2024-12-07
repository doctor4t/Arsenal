package dev.doctor4t.arsenal.item;

public interface CustomNameColorItem {
    default int getNameColor() {
        return 0xFFFFFF;
    }
}
