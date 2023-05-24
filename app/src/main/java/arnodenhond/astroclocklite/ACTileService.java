package arnodenhond.astroclocklite;

import static androidx.wear.tiles.DimensionBuilders.expand;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.wear.tiles.EventBuilders;
import androidx.wear.tiles.LayoutElementBuilders;
import androidx.wear.tiles.RequestBuilders;
import androidx.wear.tiles.ResourceBuilders;
import androidx.wear.tiles.TileBuilders;
import androidx.wear.tiles.TileService;
import androidx.wear.tiles.TimelineBuilders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ACTileService extends TileService {

    //private static final String RESOURCES_VERSION = "1";

    private String datetimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        Date date = new Date();
        String result = sdf.format(date)+":"+date.getSeconds();
        return result;
    }

    @Override
    protected void onTileEnterEvent(@NonNull EventBuilders.TileEnterEvent requestParams) {
        super.onTileEnterEvent(requestParams);
        getUpdater(this).requestUpdate(ACTileService.class);
    }

    @NonNull
    @Override
    protected ListenableFuture<TileBuilders.Tile> onTileRequest(
            @NonNull RequestBuilders.TileRequest requestParams
    ) {
        getUpdater(this).requestUpdate(ACTileService.class);
        return Futures.immediateFuture(new TileBuilders.Tile.Builder()
                        .setResourcesVersion(datetimeStr())
                        .setFreshnessIntervalMillis(20*1000)
                        .setTimeline(new TimelineBuilders.Timeline.Builder()
                                        .addTimelineEntry(new TimelineBuilders.TimelineEntry.Builder()
                                                        .setLayout(new LayoutElementBuilders.Layout.Builder()
                                                                        .setRoot(new LayoutElementBuilders.Image.Builder()
                                                                                .setResourceId(datetimeStr())
                                                                                .setWidth(expand())
                                                                                .setHeight(expand())
                                                                                .setContentScaleMode(LayoutElementBuilders.CONTENT_SCALE_MODE_FILL_BOUNDS)
                                                                                .build()
                                                                        ).build()
//                                        .setRoot(new LayoutElementBuilders.Text.Builder()
//                                                .setText("Hello world!")
//                                                .setFontStyle(new LayoutElementBuilders.FontStyle.Builder()
//                                                        .setColor(argb(0xFF00FF00)).build()
//                                                ).build()
//                                        ).build()
                                                        ).build()
                                        ).build()
                        ).build()
        );
    }

    @NonNull
    @Override
    protected ListenableFuture<ResourceBuilders.Resources> onResourcesRequest(
            @NonNull RequestBuilders.ResourcesRequest requestParams
    ) {
        SharedPreferences prefs = getSharedPreferences("location", MODE_PRIVATE);
        double lat=0;
        double lon=0;
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Futures.immediateFuture(new ResourceBuilders.Resources.Builder()
                            .setVersion(datetimeStr())
                            .addIdToImageMapping(datetimeStr(), new ResourceBuilders.ImageResource.Builder()
                                .setAndroidResourceByResId(new ResourceBuilders.AndroidImageResourceByResId.Builder()
                                        .setResourceId(R.drawable.permission)
                                        .build()
                                )
                                    .build()
                            )
                            .build()
            );
        } else {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {


                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                float flat = (float) location.getLatitude();
                                float flon = (float) location.getLongitude();
                                prefs.edit().putFloat("lat",flat).putFloat("lon",flon).apply();
                            }
                        }
                    });
            lat = prefs.getFloat("lat",0);
            lon = prefs.getFloat("lon", 0);
            return Futures.immediateFuture(new ResourceBuilders.Resources.Builder()
                            .setVersion(datetimeStr())
                            .addIdToImageMapping(datetimeStr(), new ResourceBuilders.ImageResource.Builder()
//                                .setAndroidResourceByResId(new ResourceBuilders.AndroidImageResourceByResId.Builder()
//                                        .setResourceId(R.drawable.background)
//                                        .build()
//                                )
                                            .setInlineResource(new ResourceBuilders.InlineImageResource.Builder()
                                                    .setData(new BitmapMaker(this,450,lat,lon,0).makeBitmapBytes())
                                                    .setWidthPx(450)
                                                    .setHeightPx(450)
                                                    .setFormat(ResourceBuilders.IMAGE_FORMAT_UNDEFINED)
                                                    .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }

    }
}

