package com.example.thinh.gesturecontroller.observer;

public interface SizeSettingSubject {
    void registerObserver(SizeSettingObserver sizeSettingObserver);
    public void removeObserver(SizeSettingObserver sizeSettingObserver);
    public void notifyObservers(); // phương thức này được gọi để thông báo cho tất cả các observer một khi trạng thái của Subject được thay đổi.
}
