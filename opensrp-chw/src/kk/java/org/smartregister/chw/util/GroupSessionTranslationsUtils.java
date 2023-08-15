package org.smartregister.chw.util;

/**
 * Created by Kassim Sheghembe on 2023-08-15
 */
public class GroupSessionTranslationsUtils {

    public static String getTranslatedSessionTookPlaceResponse(String response) {
        switch (response) {
            case "Ndiyo":
                return "Yes";
            case "Hapana":
                return "No";
            default:
                return response;
        }
    }

    public static String getTranslatedSessionNotTakePlaceReason(String response) {
        switch (response) {
            case "MJA alishindwa kuendesha kipindi (aliumwa, n.k)":
                return "CHW(s) incapacitated (sickness, etc.)";
            case "Walezi wote hawakupatikana au hawakuwepo":
                return "All caregivers unavailable";
            case "Walezi wote walikataa":
                return "All caregivers refused";
            case "Watoto walishindwa kuhudhuria (waliumwa n.k)":
                return "All children incapacitated (sickness, etc.)";
            case "Sababu nyingine":
                return "Other (Specify)";
            default:
                return response;
        }
    }

}
