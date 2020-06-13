package com.tfgstuff.blueapp;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.view.Gravity;
import android.widget.Toast;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    /*
    PATTERN_PASSWORD explanation

                 ^                 # start-of-string
                 (?=.*[0-9])       # a digit must occur at least once
                 (?=.*[a-z])       # a lower case letter must occur at least once
                 (?=.*[A-Z])       # an upper case letter must occur at least once
                 (?=.*[@#$%^&+=|"()?¿¡!'*._,;:])  # a special character must occur at least once
                 .{8,}             # anything, at least eight places though
                 $                 # end-of-string

     */

    private static final String PATTERN_LOCALDATETIME = "yyyy-MM-dd HH:mm:ss";
    private static final String PATTERN_PHONE = "^(?=.*[0-9]).{9,13}$";
    private static final String PATTERN_DATE = "yyyy/MM/dd";
    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PATTERN_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=|\"()?¿¡!'*._,;:]).{8,}$";


    public static boolean hasCompletePasswordFormat(String password) {
        Pattern pattern = Pattern.compile(PATTERN_PASSWORD);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean hasPhoneFormat(String phone) {
        Pattern pattern = Pattern.compile(PATTERN_PHONE);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static boolean hasEmailFormat(String email) {
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static Date stringToCustomDate(String date, String customPattern) {
        DateFormat format = new SimpleDateFormat(customPattern);
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }


    public static Date stringToDate(String date) {
        DateFormat format = new SimpleDateFormat(PATTERN_DATE);
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static LocalDate stringToLocalDate(String date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, dtf);
    }

    public static int calculateAge(String birthDate) {
        LocalDate birth = LocalDate.parse(birthDate);
        LocalDate now = LocalDate.now();
        return Period.between(birth, now).getYears();
    }

    public static String timeBetween(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(date, now);
        String time = "";
        time = checkDuration(duration, time);
        return time;
    }

    private static String checkDuration(Duration duration, String time) {
        if (duration.toMinutes() < 1) {
            time = "Hace menos de un minuto";
        } else if (duration.toMinutes() >= 1 && duration.toMinutes() < 60) {
            if (duration.toMinutes() == 1) {
                time = "Hace un minuto";
            } else {
                time = "Hace " + duration.toMinutes() + " minutos";
            }
        } else if (duration.toMinutes() >= 60 && duration.toHours() < 24) {
            if (duration.toHours() == 1) {
                time = "Hace una hora";
            } else {
                time = "Hace " + duration.toHours() + " horas";
            }
        } else if (duration.toHours() >= 24) {
            if (duration.toDays() == 1) {
                time = "Hace un día";
            } else {
                time = "Hace " + duration.toDays() + " días.";
            }
        }
        return time;
    }

    public static String localDateTimeToDisplayString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_LOCALDATETIME);
        String newDate = date.format(formatter);
        return newDate;
    }

    public static LocalDateTime stringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_LOCALDATETIME);
        LocalDateTime newDate = LocalDateTime.parse(date, formatter);
        return newDate;
    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String SHA256(String text) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(text.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; i++) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static ArrayList<String> extractUrls(String text) {
        ArrayList<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }
        return containedUrls;
    }

//    -------------------------------ANDROID METHODS------------------------------------------------

    public static void toast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void copyToClipboard(Context context, String text) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("clip", text);
        clipboard.setPrimaryClip(clip);
    }

    public static void downloadFileFromUri(Context context, String fileName, String fileExtension, String destinationDirectory, Uri uri) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }

    public static void downloadFileFromUrl(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }
}
