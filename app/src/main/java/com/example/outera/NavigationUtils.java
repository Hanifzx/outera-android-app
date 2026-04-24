package com.example.outera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

public class NavigationUtils {
    public static void setupBottomNav(Activity activity, int currentMenuId) {
        LinearLayout navBeranda = activity.findViewById(R.id.nav_beranda);
        LinearLayout navKatalog = activity.findViewById(R.id.nav_katalog);
        LinearLayout navSewa = activity.findViewById(R.id.nav_sewa);
        LinearLayout navRiwayat = activity.findViewById(R.id.nav_riwayat);
        LinearLayout navLaporan = activity.findViewById(R.id.nav_laporan);

        if (navBeranda == null) return;

        resetNav(navBeranda, activity.findViewById(R.id.text_beranda), activity.findViewById(R.id.icon_beranda));
        resetNav(navKatalog, activity.findViewById(R.id.text_katalog), activity.findViewById(R.id.icon_katalog));
        resetNav(navSewa, activity.findViewById(R.id.text_sewa), activity.findViewById(R.id.icon_sewa));
        resetNav(navRiwayat, activity.findViewById(R.id.text_riwayat), activity.findViewById(R.id.icon_riwayat));
        resetNav(navLaporan, activity.findViewById(R.id.text_laporan), activity.findViewById(R.id.icon_laporan));

        if (currentMenuId == R.id.nav_beranda) setActive(navBeranda, activity.findViewById(R.id.text_beranda), activity.findViewById(R.id.icon_beranda));
        else if (currentMenuId == R.id.nav_katalog) setActive(navKatalog, activity.findViewById(R.id.text_katalog), activity.findViewById(R.id.icon_katalog));
        else if (currentMenuId == R.id.nav_sewa) setActive(navSewa, activity.findViewById(R.id.text_sewa), activity.findViewById(R.id.icon_sewa));
        else if (currentMenuId == R.id.nav_riwayat) setActive(navRiwayat, activity.findViewById(R.id.text_riwayat), activity.findViewById(R.id.icon_riwayat));
        else if (currentMenuId == R.id.nav_laporan) setActive(navLaporan, activity.findViewById(R.id.text_laporan), activity.findViewById(R.id.icon_laporan));

        navBeranda.setOnClickListener(v -> navigateTo(activity, currentMenuId, R.id.nav_beranda, DashboardActivity.class));
        navKatalog.setOnClickListener(v -> navigateTo(activity, currentMenuId, R.id.nav_katalog, KatalogActivity.class));
        navSewa.setOnClickListener(v -> navigateTo(activity, currentMenuId, R.id.nav_sewa, SewaActivity.class));
        navRiwayat.setOnClickListener(v -> navigateTo(activity, currentMenuId, R.id.nav_riwayat, RiwayatActivity.class));
        navLaporan.setOnClickListener(v -> navigateTo(activity, currentMenuId, R.id.nav_laporan, LaporanActivity.class));
    }

    private static void resetNav(LinearLayout layout, TextView text, ImageView icon) {
        layout.setBackgroundResource(0);
        text.setTextColor(Color.parseColor("#77786B"));
        icon.setColorFilter(Color.parseColor("#77786B"));
    }

    private static void setActive(LinearLayout layout, TextView text, ImageView icon) {
        layout.setBackgroundResource(R.drawable.bg_nav_item_active);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        icon.setColorFilter(Color.parseColor("#FFFFFF"));
    }

    private static void navigateTo(Activity currentActivity, int currentMenuId, int targetMenuId, Class<?> targetClass) {
        if (currentMenuId == targetMenuId) return;
        Intent intent = new Intent(currentActivity, targetClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        currentActivity.startActivity(intent);
        currentActivity.finish();
        currentActivity.overridePendingTransition(0, 0);
    }
}
