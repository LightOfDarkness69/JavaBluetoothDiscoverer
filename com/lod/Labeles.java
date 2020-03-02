package com.lod;

/**
 * This enum implements localisation. (English and Russian)
 *
 * Usage: Labeles.NAME_OF_LABELE.getValue(language)
 */

public enum Labeles {

    SEARCHING("Searching for devices", "Поиск устройст"),
    PREPARING("Searching will start in ", "Поиск начнется через"),

    LOCALDEVICA_INFO("  ~~Local Device Information~~\n\n", "Локальная информация\n\n"),

    LOCALDEVICE_NAME("Name: ", "Имя: "),
    LOCALDEVICE_ADDRESS("Address: ", "Адрес: "),
    LOCALDEVICE_TYPE("Device type: ", "Тип устройства: "),
    LOCALDEVICE_SUBTYPE("Device subtype: ", "Под-тип устройства: "),
    LOCALDEVICE_SERVICES("Services: ", "Профили: "),
    LOCALDEVICE_STATUS("Status: ", "Статус: "),
    LOCALDEVICE_DISCOVERABLE("Discoverable", "Обнаружаемый"),
    LOCALDEVICE_NOTDISCOVERABLE("Not discoverable", "Не обнаружаемый"),
    LOCALDEVICE_MODE("Mode: ", "Состояние: "),
    LOCALDEVICE_NOTRUNNING("Not running", "Не работает"),
    LOCALDEVICE_RUNNING("Running ", "Работает"),

    REMOTEDEVICE_ADDRESS("Address: ", "Адрес: "),
    REMOTEDEVICE_NAME("Name: ", "Имя: "),
    REMOTEDEVICE_NAMEUNKNOWN("Unknown", "Неизвестно"),

    COMPUTER("Computer", "Компьютер"),
    PHONE("Phone", "Телефон"),
    PC("Personal computer", "Персональный компьютер"),
    LAPTOP("Laptop", "Ноутбук"),

    DEVICES("Bluetooth-devices nearby: \n", "Bluetooth-устройства поблизости: \n");




    private String engV, rusV;

    private Labeles(String engV, String rusV) {
        this.engV = engV;
        this.rusV = rusV;
    }


    public String getValue(String language) {
        switch (language) {
            case "rus":
                return rusV;
            case "eng":
                return engV;
        }

        return "Language is not selected!";
    }

}

