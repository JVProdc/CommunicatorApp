package cz.test.test1;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.opencsv.CSVWriter;
import com.soundcloud.android.crop.Crop;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class MainActivity extends AppCompatActivity implements GalleryAdapter.OnItemClickListener {
    RecyclerView recyclerView;

    ArrayList<String> images;
    GalleryAdapter adapter;
    GridLayoutManager manager;
    private HashMap<String, String> imageLabelMap;
    private ArrayList<String> imageLables;
    public int[] ids;
    public String[] labels;
    public int[] counts;
    public String[] imageNames;
    private int dataCount = 0;
    private final ArrayList<String> selectedImages = new ArrayList<>();
    private LinearLayout selectedImagesContainer;

    private ImageButton deleteButton; // Added this line
    private static final int PERMISSION_REQUEST_CODE = 100;
    TextToSpeech tts;
    public boolean speak = true;
    public boolean lanEn = false;
    String StringCancel = "Cancel";
    String StringMaxImages = "Maximum number of images: ";
    String StringEmptyLabel = "Label can not be empty";

    public boolean clientRegistered = false;
    public String Useremail = null;
    public String Userusername = null;
    private static final String SETTINGS_FILE = "app_settings.json";
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SELECT_FILE = 2;
    int desiredColumnWidthDP = 200;
    int desiredColumnHeightDP = 230;
    int desiredImageWidthDP = 200;
    int dpWidth = 200; // Initial width in dp
    int dpHeight = 200; // Initial height in dp


    public boolean displayImages = true;


    public int ColumnsNumber = 0;

    public int colorCellSizeDp = 120;


    public int[] colorResources = {R.drawable.tick, R.drawable.cross, R.drawable.black, R.drawable.brown, R.drawable.blue, R.drawable.green, R.drawable.pink, R.drawable.red, R.drawable.orange, R.drawable.yellow, R.drawable.white};
    public String[] colorTTScz = {"Ano", "Ne", "černá", "hnědá", "modrá", "zelená", "růžová", "červená", "oranžová", "žlutá", "bílá"};
    public String[] colorTTSen = {"Yes", "No", "black", "brown", "blue", "green", "pink", "red", "orange", "yellow", "white"};

    public String[] dayen = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    public String[] daycz = {"Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota", "Neděle"};

    public int[] numberResources = {R.drawable.num_0, R.drawable.num_1, R.drawable.num_2, R.drawable.num_3, R.drawable.num_4, R.drawable.num_5, R.drawable.num_6, R.drawable.num_7, R.drawable.num_8, R.drawable.num_9, R.drawable.num_10};
    public String[] numTTS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    public int ColNumSel = 0;

    float lvol = 0.2f;
    float rvol = 0.2f;
    public int ColNumSize;

    public int numColumns = 0;

    private boolean isDividedLayout = false;

    Map<String, List<String>> dayImages = new HashMap<>();


    private File destinationFile;
    String[] cz = {"Přidat Obrázek", "Jazyk: CZ", "Přidat PDF", "Seřadit Obrázky", "Hlas Zapnutý", "Hlas Vypnutý", "Zrušit", "Popisek nemůže být prázdný", "Zamítli jste oprávnění", "Maximální počet obrázků: ", "Přidat sestavu", "Exportovat sestavu", "Můj plán", "OK", "Další obrázek", "Další PDF", "Are you sure to delete the image?", "Image ", " was deleted.", "Yes", "No", "Battery: "};
    String[] en = {"Add Image", "Language: EN", "Add PDF", "Sort Image", "Voice Assist: ON", "Voice Assist: OFF", "Cancel", "Label can not be empty", "You have dined the permission", "Maximum number of images: ", "Add Bundle", "Export Bundle", "My plan", "OK", "Next Image", "Next PDF", "Opravdu chcete obrázek odstranit?", "Obrázek ", " byl odstraněn", "Ano", "Ne", "Baterie: "}; //16


    public int SelectedDay = 0;


    private final Handler handlerSortCSV = new Handler();

    private final Runnable sortCSVRunnable = new Runnable() {
        @Override
        public void run() {
            // Call your method to sort CSV by count
            sortCSVByCount();
            sortImagesByCount();
            Log.i("Main Activity", "Sorted by the Timer");

            // Schedule the task to run again after an minute
            handlerSortCSV.postDelayed(this, TimeUnit.MINUTES.toMillis(10));
        }
    };


    private AlertDialog registrationDialog;

    List<String> mondayImages = new ArrayList<>();
    List<String> tuesdayImages = new ArrayList<>();
    List<String> wednesdayImages = new ArrayList<>();
    List<String> thursdayImages = new ArrayList<>();
    List<String> fridayImages = new ArrayList<>();
    List<String> saturdayImages = new ArrayList<>();
    List<String> sundayImages = new ArrayList<>();

    ArrayList<String> dayArray = new ArrayList<>();

    public int currentDayOfWeek = DateUtils.getCurrentDayOfWeek();

    private AlertDialog dialog;

    public String pathToLastAddedImage;

    private TextView batteryTextView;
    private BroadcastReceiver batteryReceiver;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedImagesContainer = findViewById(R.id.selectedImagesContainer);
        deleteButton = findViewById(R.id.deleteButton);
        recyclerView = findViewById(R.id.gallery_recycler);


        batteryTextView = findViewById(R.id.battery_percentage);


        images = new ArrayList<>();
        imageLables = new ArrayList<>();
        adapter = new GalleryAdapter(this, images, itemClickListener, itemLongClickListener, imageLables);
        imageLabelMap = new HashMap<>();

        setupRecyclerView();

        if (isTablet()) {
            desiredColumnWidthDP = 200;
            ColNumSize = 200;
            colorCellSizeDp = 120;
        } else {
            desiredColumnWidthDP = 120;
            ColNumSize = 120;
            colorCellSizeDp = 60;
        }

        GridView colorGrid = findViewById(R.id.colorGrid);
        ColorGridAdapter colorGridAdapter = new ColorGridAdapter(this, colorResources, colorCellSizeDp, this, true); // true indicates color button
        colorGrid.setAdapter(colorGridAdapter);

        GridView numGrid = findViewById(R.id.numGrid);
        ColorGridAdapter numGridAdapter = new ColorGridAdapter(this, numberResources, colorCellSizeDp, this, false); // false indicates number button
        numGrid.setAdapter(numGridAdapter);

        // Calculate the number of columns based on the screen width and desired column width
        float density = getResources().getDisplayMetrics().density;
        int columnWidth = (int) (desiredColumnWidthDP * density);
        int columnHeight = (int) (desiredColumnHeightDP * density);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int numColumns = screenWidth / columnWidth;
        ColumnsNumber = numColumns - 1;

        recyclerView.setAdapter(adapter);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteButtonClick();
            }
        });

        deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                handleDeleteButtonLongClick();
                return true;
            }
        });



        batteryReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                if (level >= 0 && scale > 0) {
                    int batteryPct = (level * 100) / scale;
                    if(lanEn) {
                        batteryTextView.setText( cz[21] + batteryPct + "%");
                    } else {
                        batteryTextView.setText( en[21] + batteryPct + "%");
                    }

                }
            }
        };

        // Register the BroadcastReceiver for battery changes
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));



        checkPermissionsRead();
        loadSettings();
        sortCSVByCount();
        sortImagesByCount();
        readCSV();
        entsureImagesFolderExists();
        createEmptyPlanCSV();
        loadLabelsFromCSV();
        loadImagesFromPlanCSV();
        loadImages(imageLables);
        startPeriodicTask();
        Toast.makeText(this, "Today is: " + getTodayDay(), Toast.LENGTH_SHORT).show();
        SelectedDay = currentDayOfWeek;

        if (!clientRegistered) {
            registerClient();
        }
    }

    private void fetchBatteryLevel() {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (intent != null) {
            batteryReceiver.onReceive(this, intent); // Manually trigger onReceive
        }
    }

    private void startPeriodicTask() {
        handlerSortCSV.postDelayed(sortCSVRunnable, TimeUnit.DAYS.toMillis(1)); // Initial delay, run every 1 minute for testing
    }

    private boolean isTablet() {
        Configuration config = getResources().getConfiguration();
        return (config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    private void showSettingsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view, Gravity.RIGHT);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.main_menu, popupMenu.getMenu());

        MenuItem switchItem = popupMenu.getMenu().findItem(R.id.switch_item);


        MenuItem languageItem = popupMenu.getMenu().findItem(R.id.menu_language);
        if (lanEn) {
            languageItem.setTitle(en[1]);
            popupMenu.getMenu().findItem(R.id.add_image).setTitle(en[0]);
            popupMenu.getMenu().findItem(R.id.add_pdf).setTitle(en[2]);
            popupMenu.getMenu().findItem(R.id.sort_image).setTitle(en[3]);
            popupMenu.getMenu().findItem(R.id.add_bundle).setTitle(en[10]);
            popupMenu.getMenu().findItem(R.id.export_bundle).setTitle(en[11]);
            popupMenu.getMenu().findItem(R.id.calendar).setTitle(en[12]);

            if (speak) {
                popupMenu.getMenu().findItem(R.id.switch_item).setTitle(en[4]);
            } else {
                popupMenu.getMenu().findItem(R.id.switch_item).setTitle(en[5]);
            }
            StringCancel = en[6];
            StringMaxImages = en[9];
            StringEmptyLabel = en[7];


        } else {
            languageItem.setTitle(cz[1]);
            popupMenu.getMenu().findItem(R.id.add_image).setTitle(cz[0]);
            popupMenu.getMenu().findItem(R.id.add_pdf).setTitle(cz[2]);
            popupMenu.getMenu().findItem(R.id.sort_image).setTitle(cz[3]);
            popupMenu.getMenu().findItem(R.id.add_bundle).setTitle(cz[10]);
            popupMenu.getMenu().findItem(R.id.export_bundle).setTitle(cz[11]);
            popupMenu.getMenu().findItem(R.id.calendar).setTitle(cz[12]);

            if (speak) {
                popupMenu.getMenu().findItem(R.id.switch_item).setTitle(cz[4]);
            } else {
                popupMenu.getMenu().findItem(R.id.switch_item).setTitle(cz[5]);
            }
            StringCancel = cz[6];
            StringMaxImages = cz[9];
            StringEmptyLabel = cz[7];


        }


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.add_image) {
                    // Handle "Add Image" action here
                    selectImageFromGallery();
                    return true;
                } else if (itemId == R.id.menu_language) {
                    lanEn = !lanEn;
                    saveSettings();
                    fetchBatteryLevel();

                    if (lanEn) {
                        item.setTitle(en[1]);
                        popupMenu.getMenu().findItem(R.id.add_pdf).setTitle(en[2]);
                        popupMenu.getMenu().findItem(R.id.sort_image).setTitle(en[3]);
                        popupMenu.getMenu().findItem(R.id.calendar).setTitle(en[12]);
                        StringCancel = en[6];
                        if (speak) {
                            popupMenu.getMenu().findItem(R.id.switch_item).setTitle(en[4]);
                        } else {
                            popupMenu.getMenu().findItem(R.id.switch_item).setTitle(en[5]);
                        }

                    } else {
                        item.setTitle(cz[1]);
                        popupMenu.getMenu().findItem(R.id.add_pdf).setTitle(cz[2]);
                        popupMenu.getMenu().findItem(R.id.sort_image).setTitle(cz[3]);
                        popupMenu.getMenu().findItem(R.id.calendar).setTitle(cz[12]);
                        StringCancel = cz[6];
                        if (speak) {
                            popupMenu.getMenu().findItem(R.id.switch_item).setTitle(cz[4]);
                        } else {
                            popupMenu.getMenu().findItem(R.id.switch_item).setTitle(cz[5]);
                        }
                    }
                    return true;
                } else if (itemId == R.id.add_pdf) {

                    pickPdfFile();

                    return true;
                } else if (itemId == R.id.add_bundle) {

                    addBundleTrans();


                } else if (itemId == R.id.export_bundle) {

                    exportBundle();

                } else if (itemId == R.id.sort_image) {
                    // Handle "Sort" action here
                    sortCSVByCount();
                    sortImagesByCount();


                    Toast.makeText(MainActivity.this, "Images Sorted", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.calendar) {
                    if (isDividedLayout) {
                        switchToCurrentLayout();
                    } else {
                        switchToDividedLayout();
                    }


                } else if (itemId == R.id.switch_item) {
                    // Toggle the speak boolean
                    speak = !speak;
                    saveSettings();

                    if (lanEn) {
                        // Update the title based on the new state
                        if (speak) {
                            switchItem.setTitle(en[4]);
                            Toast.makeText(MainActivity.this, en[4], Toast.LENGTH_SHORT).show();
                        } else {
                            switchItem.setTitle(en[5]);
                            Toast.makeText(MainActivity.this, en[5], Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (speak) {
                            switchItem.setTitle(cz[4]);
                            Toast.makeText(MainActivity.this, cz[4], Toast.LENGTH_SHORT).show();
                        } else {
                            switchItem.setTitle(cz[5]);
                            Toast.makeText(MainActivity.this, cz[5], Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;

                } else {
                    return false;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    private void setupRecyclerView() {
        // Calculate the number of columns based on the screen width and desired column width
        float density = getResources().getDisplayMetrics().density;
        int desiredColumnWidthDP;
        if (isTablet()) {
            desiredColumnWidthDP = 200;
        } else {
            desiredColumnWidthDP = 120;
        }
        int columnWidth = (int) (desiredColumnWidthDP * density);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        numColumns = screenWidth / columnWidth;

        // Subtracting 1 from numColumns because you want the RecyclerView to occupy half of the screen
        numColumns = Math.max(numColumns - 1, 1);

        // Set the GridLayoutManager with the calculated number of columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, numColumns);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initializeMainActivity() {
        // Set the content view to the main activity layout
        setContentView(R.layout.activity_main);

        // Find views
        selectedImagesContainer = findViewById(R.id.selectedImagesContainer);
        deleteButton = findViewById(R.id.deleteButton);
        recyclerView = findViewById(R.id.gallery_recycler);


        // Initialize other views and adapters
        GridView colorGrid = findViewById(R.id.colorGrid);
        ColorGridAdapter colorGridAdapter = new ColorGridAdapter(this, colorResources, colorCellSizeDp, this, true);
        colorGrid.setAdapter(colorGridAdapter);

        GridView numGrid = findViewById(R.id.numGrid);
        ColorGridAdapter numGridAdapter = new ColorGridAdapter(this, numberResources, colorCellSizeDp, this, false);
        numGrid.setAdapter(numGridAdapter);

        // Calculate the number of columns based on the screen width and desired column width
        float density = getResources().getDisplayMetrics().density;
        int columnWidth = (int) (desiredColumnWidthDP * density);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int numColumns = screenWidth / columnWidth;
        ColumnsNumber = numColumns - 1;

        // Set up RecyclerView
        setupRecyclerView();

        // Set the layout manager for the RecyclerView
        manager = new GridLayoutManager(this, ColumnsNumber);
        recyclerView.setLayoutManager(manager);


        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);

        // Set click and long-click listeners for the delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteButtonClick();
            }
        });

        deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                handleDeleteButtonLongClick();
                return true;
            }
        });
        // Perform other initialization steps (e.g., permission checks, loading settings, etc.)
        loadSettings();
        sortCSVByCount();
        sortImagesByCount();
        readCSV();
        loadLabelsFromCSV();
        loadImages(imageLables);
    }

    private void initializeDividedLayout() {
        RadioGroup radioGroup = findViewById(R.id.dayGroup);
        deleteButton = findViewById(R.id.deleteButton);
        recyclerView = findViewById(R.id.gallery_recycler);


        // Set click listener for the delete button to open the popup menu
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteButtonClick();
            }
        });

        deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                handleDeleteButtonLongClick();
                return true;
            }
        });

        // Initialize RecyclerView for the divided layout
        recyclerView = findViewById(R.id.recyclerView); // Assuming the RecyclerView ID is "gallery_recycler" in both layouts
        LinearLayout selectedView = findViewById(R.id.selectedView);

        // Calculate the number of columns based on the screen width and desired column width
        float density = getResources().getDisplayMetrics().density;
        int columnWidth = (int) (desiredColumnWidthDP * density);
        int columnHeight = (int) (desiredColumnHeightDP * density);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int numColumns = screenWidth / columnWidth;
        ColumnsNumber = numColumns / 2;

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new GridLayoutManager(this, ColumnsNumber);
        recyclerView.setLayoutManager(layoutManager);

        // Set up adapter and attach to RecyclerView
        adapter = new GalleryAdapter(this, images, itemClickListener, itemLongClickListener, imageLables);
        recyclerView.setAdapter(adapter);

        RadioButton radioButton = findViewById(getRadioButtonIdForDay(currentDayOfWeek));
        radioButton.setChecked(true);

        setDayButtonsText();


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                String selectedDay = radioButton.getText().toString();
                //Toast.makeText(MainActivity.this, "Today is " + getTodayDay(), Toast.LENGTH_SHORT).show();

                // Handle the selection based on the selected day
                if (checkedId == R.id.monday) {
                    SelectedDay = 0;
                    selectedView.removeAllViews();


                } else if (checkedId == R.id.tuesday) {
                    SelectedDay = 1;
                    selectedView.removeAllViews();


                } else if (checkedId == R.id.wednesday) {
                    SelectedDay = 2;
                    selectedView.removeAllViews();


                } else if (checkedId == R.id.thursday) {
                    SelectedDay = 3;
                    selectedView.removeAllViews();


                } else if (checkedId == R.id.friday) {
                    SelectedDay = 4;
                    selectedView.removeAllViews();

                } else if (checkedId == R.id.saturday) {
                    SelectedDay = 5;
                    selectedView.removeAllViews();

                } else if (checkedId == R.id.sunday) {
                    SelectedDay = 6;
                    selectedView.removeAllViews();
                }
                displayImagesByDay(SelectedDay);
            }
        });
    }

    public String getTodayDay() {
        String today = null;
        int transDay = 0;

        if (currentDayOfWeek == 0) {
            transDay = 6;
        } else {
            transDay = currentDayOfWeek - 1;
        }


        if (lanEn == true) {

            today = dayen[transDay];
        } else {
            today = daycz[transDay];
        }


        return today;
    }


    private void switchLayout(boolean isDividedLayout) {
        // Inflate the appropriate layout based on the value of isDividedLayout
        int layoutResId = isDividedLayout ? R.layout.calendar : R.layout.activity_main;
        setContentView(layoutResId);

        // If using the divided layout, find the menu button again
        if (isDividedLayout) {
            deleteButton = findViewById(R.id.deleteButton);
            // Set click listener for the menu button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the popup menu
                    showSettingsMenu(deleteButton);
                }
            });
        }

    }

    private void switchToDividedLayout() {
        isDividedLayout = true;
        switchLayout(isDividedLayout);
        initializeDividedLayout();

        displayImagesForCurrentDay();

    }

    private void switchToCurrentLayout() {
        isDividedLayout = false;
        switchLayout(isDividedLayout);
        initializeMainActivity();
        writePlanCSV();
    }


    private void displayImagesByDay(int selectedDay) {

        switch (selectedDay) {
            case 0:
                dayArray = (ArrayList) mondayImages;
                break;
            case 1:
                dayArray = (ArrayList) tuesdayImages;
                break;
            case 2:
                dayArray = (ArrayList) wednesdayImages;
                break;
            case 3:
                dayArray = (ArrayList) thursdayImages;
                break;
            case 4:
                dayArray = (ArrayList) fridayImages;
                break;
            case 5:
                dayArray = (ArrayList) saturdayImages;
                break;
            case 6:
                dayArray = (ArrayList) sundayImages;
                break;

        }

        if (dayArray != null && !dayArray.isEmpty()) {
            //Display images from the selected day array
            Log.e("Main Activity", "Selected dayArray: " + dayArray);
            displayDividedImages(dayArray);
        } else {
            // Handle the case where the selected day array is empty
            Toast.makeText(this, "No images found for " + selectedDay, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayDividedImages(List<String> imagePaths) {

        if (imagePaths.size() > 9) {
            imagePaths = imagePaths.subList(0, 9);
        }

        LinearLayout selectedView = findViewById(R.id.selectedView);

        // Clear existing views from the LinearLayout
        if (selectedView != null) {
            Log.d(TAG, "selectedView is not null");
            selectedView.removeAllViews();
        }
        // Calculate image width based on device type
        int desiredImageWidthDP;
        int desiredImageHeightDP;
        if (isTablet()) {
            desiredImageWidthDP = 150; // Adjust for tablets
            desiredImageHeightDP = 150;
        } else {
            desiredImageWidthDP = 100; // Adjust for phones
            desiredImageHeightDP = 100;
        }

        // Convert dp to pixels
        float density = getResources().getDisplayMetrics().density;
        int imageWidth = (int) (desiredImageWidthDP * density);
        int imageHeight = (int) (desiredImageHeightDP * density);

        // Calculate maximum number of images that can fit in one row
        int maxImagesPerRow = selectedView.getWidth() / (imageWidth + 2 * 8); // Account for margins
        int maxImagesPerCol = selectedView.getHeight() / (imageHeight + 2 * 8); // Account for margins
        Toast.makeText(this, "maxImagesPerRow: " + maxImagesPerRow, Toast.LENGTH_SHORT).show();

        // Ensure maxImagesPerRow is at least 1 to avoid division by zero
        maxImagesPerRow = Math.max(maxImagesPerRow, 1);

        // Initialize variables for organizing images into rows
        int numImages = imagePaths.size();
        int numImagesDisplayed = 0;

        // Loop until all images are displayed
        while (numImagesDisplayed < numImages) {
            // Create a new LinearLayout for each row
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Calculate the number of images to display in this row
            int numImagesInRow = Math.min(maxImagesPerRow, numImages - numImagesDisplayed);

            // Loop through the images in this row
            for (int i = numImagesDisplayed; i < numImagesDisplayed + numImagesInRow; i++) {
                String imagePath = imagePaths.get(i);

                // Create a new ImageView for each image
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, imageHeight);
                params.setMargins(8, 8, 8, 8); // Adjust margins as needed

                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                // Load the image into the ImageView using Glide
                Glide.with(this).load(imagePath).into(imageView);

                // Add the ImageView to the current row
                rowLayout.addView(imageView);
            }

            // Add the row to the selectedView LinearLayout
            selectedView.addView(rowLayout);

            // Update the number of images displayed
            numImagesDisplayed += numImagesInRow;
        }
    }


    private void displayImagesForCurrentDay() {
        // Based on the current day of the week, display images
        switch (currentDayOfWeek) {
            case 0:
                displayDividedImages(mondayImages);
                break;
            case 1:
                displayDividedImages(tuesdayImages);
                break;
            case 2:
                displayDividedImages(wednesdayImages);
                break;
            case 3:
                displayDividedImages(thursdayImages);
                break;
            case 4:
                displayDividedImages(fridayImages);
                break;
            case 5:
                displayDividedImages(saturdayImages);
                break;
            case 6:
                displayDividedImages(sundayImages);
                break;
            default:
                // Handle unexpected day
                break;
        }
    }


    private void createEmptyPlanCSV() {
        File planCSVFile = new File(getExternalFilesDir(null), "plan.csv");

        try {
            if (!planCSVFile.exists()) {
                // Create the CSV file if it doesn't exist
                planCSVFile.createNewFile();

                // Initialize the CSV file with the header only if it's a new file
                FileWriter writer = new FileWriter(planCSVFile, true);
                if (planCSVFile.length() == 0) {
                    writer.append("day,imagePath\n"); // Modify this header based on your data structure
                }
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Main Activity", "Failed to create plan.csv");
        }
    }

    private void writePlanCSV() {
        StringBuilder csvData = new StringBuilder();
        // Clear existing data in day arrays
        clearPlanCSV();

        // Write the header
        csvData.append("day,imagePath\n");

        // Write data for each day
        writeDayToCSV(mondayImages, "Monday", csvData);
        writeDayToCSV(tuesdayImages, "Tuesday", csvData);
        writeDayToCSV(wednesdayImages, "Wednesday", csvData);
        writeDayToCSV(thursdayImages, "Thursday", csvData);
        writeDayToCSV(fridayImages, "Friday", csvData);
        writeDayToCSV(saturdayImages, "Saturday", csvData);
        writeDayToCSV(sundayImages, "Sunday", csvData);

        // Write the data to plan.csv
        try {
            File planCSVFile = new File(getExternalFilesDir(null), "plan.csv");
            if (!planCSVFile.exists()) {
                planCSVFile.createNewFile();
            }

            FileWriter writer = new FileWriter(planCSVFile);
            writer.write(csvData.toString());
            writer.close();

            // Set appropriate permissions for the file
            planCSVFile.setReadable(true);
            planCSVFile.setWritable(true);

            Log.i("Main Activity", "Data successfully written to plan.csv");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Main Activity", "Failed to write data to plan.csv");
        }
    }

    private void writeDayToCSV(List<String> imagePaths, String day, StringBuilder csvData) {
        // Append the day to the CSV data
        csvData.append(day).append(",");

        // Append the image paths for the day
        for (String imagePath : imagePaths) {
            csvData.append(imagePath).append(" ");
        }

        // Add a new line after the day's image paths
        csvData.append("\n");
    }

    private void loadImagesFromPlanCSV() {

        // Read the plan.csv file
        try {
            File planCSVFile = new File(getExternalFilesDir(null), "plan.csv");
            if (planCSVFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(planCSVFile));
                String line;

                // Skip the header line
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    // Split the line into day and image paths
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String day = parts[0].trim();
                        String[] imagePaths = parts[1].trim().split(" ");

                        // Populate the corresponding day array with image paths
                        switch (day) {
                            case "Monday":
                                addPathsToDayArray(mondayImages, imagePaths);
                                break;
                            case "Tuesday":
                                addPathsToDayArray(tuesdayImages, imagePaths);
                                break;
                            case "Wednesday":
                                addPathsToDayArray(wednesdayImages, imagePaths);
                                break;
                            case "Thursday":
                                addPathsToDayArray(thursdayImages, imagePaths);
                                break;
                            case "Friday":
                                addPathsToDayArray(fridayImages, imagePaths);
                                break;
                            case "Saturday":
                                addPathsToDayArray(saturdayImages, imagePaths);
                                break;
                            case "Sunday":
                                addPathsToDayArray(sundayImages, imagePaths);
                                break;
                            default:
                                // Handle unknown day
                                break;
                        }
                    }
                }

                // Log success message
                Log.i("Main Activity", "Images loaded from plan.csv");
            } else {
                Log.i("Main Activity", "plan.csv does not exist");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearPlanCSV() {
        try {
            File planCSVFile = new File(getExternalFilesDir(null), "plan.csv");
            if (planCSVFile.exists()) {
                // Delete the existing CSV file
                planCSVFile.delete();

                // Recreate the empty CSV file
                planCSVFile.createNewFile();

                // Set appropriate permissions for the new file
                planCSVFile.setReadable(true);
                planCSVFile.setWritable(true);

                Log.i("Main Activity", "plan.csv cleared successfully");
                Log.e(TAG, mondayImages.toString());
                Log.e(TAG, tuesdayImages.toString());
                Log.e(TAG, wednesdayImages.toString());
                Log.e(TAG, thursdayImages.toString());
                Log.e(TAG, fridayImages.toString());
                Log.e(TAG, saturdayImages.toString());
                Log.e(TAG, sundayImages.toString());

                createEmptyPlanCSV();
            } else {
                Log.i("Main Activity", "plan.csv does not exist");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Main Activity", "Failed to clear plan.csv");
        }
    }

    private void addPathsToDayArray(List<String> dayArray, String[] imagePaths) {
        // Add image paths to the day array
        for (String imagePath : imagePaths) {
            dayArray.add(imagePath);
        }
    }

    private int getRadioButtonIdForDay(int dayIndex) {
        switch (dayIndex) {
            case 1:
                return R.id.monday;
            case 2:
                return R.id.tuesday;
            case 3:
                return R.id.wednesday;
            case 4:
                return R.id.thursday;
            case 5:
                return R.id.friday;
            case 6:
                return R.id.saturday;
            case 0:
                return R.id.sunday;
            default:
                return -1; // Invalid day index
        }
    }

    private void setDayButtonsText() {
        // Set the text for each RadioButton dynamically
        RadioButton monday = findViewById(R.id.monday);
        RadioButton tuesday = findViewById(R.id.tuesday);
        RadioButton wednesday = findViewById(R.id.wednesday);
        RadioButton thursday = findViewById(R.id.thursday);
        RadioButton friday = findViewById(R.id.friday);
        RadioButton saturday = findViewById(R.id.saturday);
        RadioButton sunday = findViewById(R.id.sunday);

        if (lanEn) {
            monday.setText(dayen[0]);
            tuesday.setText(dayen[1]);
            wednesday.setText(dayen[2]);
            thursday.setText(dayen[3]);
            friday.setText(dayen[4]);
            saturday.setText(dayen[5]);
            sunday.setText(dayen[6]);
        } else {
            monday.setText(daycz[0]);
            tuesday.setText(daycz[1]);
            wednesday.setText(daycz[2]);
            thursday.setText(daycz[3]);
            friday.setText(daycz[4]);
            saturday.setText(daycz[5]);
            sunday.setText(daycz[6]);
        }


    }


    private void handleDeleteButtonClick() {
        MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.delete_sound);
        mp.setVolume(lvol, rvol);
        mp.start();

        if (selectedImages.size() != 0) {
            backButtonAction();
        }
        if (isDividedLayout) {
            LinearLayout selectedView = findViewById(R.id.selectedView);
            dayArray.clear();
            Log.e(TAG, "dayArray = " + dayArray.toString());

            if (SelectedDay == 0) {
                mondayImages.clear();
                Log.e(TAG, "mondayImages = " + mondayImages.toString());
            } else if (SelectedDay == 1) {
                tuesdayImages.clear();
                Log.e(TAG, "tuesdayImages = " + tuesdayImages.toString());
            } else if (SelectedDay == 2) {
                wednesdayImages.clear();
                Log.e(TAG, "wednesdayImages = " + wednesdayImages.toString());
            } else if (SelectedDay == 3) {
                thursdayImages.clear();
                Log.e(TAG, "thursdayImages = " + thursdayImages.toString());
            } else if (SelectedDay == 4) {
                fridayImages.clear();
                Log.e(TAG, "fridayImages = " + fridayImages.toString());
            } else if (SelectedDay == 5) {
                saturdayImages.clear();
                Log.e(TAG, "saturdayImages = " + saturdayImages.toString());
            } else if (SelectedDay == 6) {
                sundayImages.clear();
                Log.e(TAG, "sundayImages = " + sundayImages.toString());
            }


            // Clear existing views from the LinearLayout
            selectedView.removeAllViews();

        }
    }

    private void handleDeleteButtonLongClick() {
        // Perform long-press action here
        if (selectedImages.size() != 0) {
            selectedImagesContainer.removeAllViews();
            selectedImages.clear();
            adapter.updateData(images, imageLables);
            loadLabelsFromCSV();
            loadImages(imageLables);
            //adapter.notifyDataSetChanged();
            displayImages = true;

            MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.delete_sound);
            mp.setVolume(lvol, rvol);
            mp.start();

            //showSettingsMenu(deleteButton);
        } else {

            showSettingsMenu(deleteButton);
        }

    }

    public void backButtonAction() {
        // Check if there are any items to remove
        if (!selectedImages.isEmpty()) {
            // Remove the last item from selectedImages
            selectedImages.remove(selectedImages.size() - 1);

            // Remove the last view from selectedImagesContainer
            int lastIndex = selectedImagesContainer.getChildCount() - 1;
            if (lastIndex >= 0) {
                selectedImagesContainer.removeViewAt(lastIndex);
            }
        }
    }

    public void handleColorClick(int position) {


        if (displayImages && selectedImages.size() <= ColumnsNumber - 1) {
            selectedImages.add(String.valueOf(position));
            // Get the selected color image resource ID
            int colorResourceId = colorResources[position];

            MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.pop_sound);
            mp.setVolume(lvol, rvol);
            mp.start();

            // Create an ImageView for the selected color image
            ImageView colorImageView = new ImageView(this);

            // Load the image using Glide and resize it to 200x200dp
            Glide.with(this).load(colorResourceId).centerCrop().override(dpToPx(ColNumSize), dpToPx(ColNumSize))
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                    //.skipMemoryCache(true)
                    .into(colorImageView);

            // Add the selected color image to the selectedImagesContainer
            selectedImagesContainer.addView(colorImageView);
            //adapter.notifyDataSetChanged();

            // Speak the label based on language settings
            if (speak) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            tts.setLanguage(lanEn ? new Locale("en_EN") : new Locale("cs_CZ"));
                            tts.setSpeechRate(1.0f);
                            if (lanEn && position >= 0 && position < colorTTSen.length) {
                                tts.speak(colorTTSen[position], TextToSpeech.QUEUE_FLUSH, null);
                            } else if (!lanEn && position >= 0 && position < colorTTScz.length) {
                                tts.speak(colorTTScz[position], TextToSpeech.QUEUE_FLUSH, null);
                            } else {
                                // Handle the case where position is out of bounds
                                Log.e("TextToSpeech", "Invalid position: " + position);
                            }
                        }
                    }
                });
            }
        }

    }

    public void handleNumberClick(int position) {
        // Get the selected color image resource ID

        if (displayImages && selectedImages.size() <= ColumnsNumber - 1) {
            int colorResourceId = numberResources[position];
            selectedImages.add(String.valueOf(position));

            MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.pop_sound);
            mp.setVolume(lvol, rvol);
            mp.start();

            // Create an ImageView for the selected color image
            ImageView colorImageView = new ImageView(this);

            // Load the image using Glide and resize it to 200x200dp
            Glide.with(this).load(colorResourceId).centerCrop().override(dpToPx(ColNumSize), dpToPx(ColNumSize))
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                    //.skipMemoryCache(true)
                    .into(colorImageView);

            // Add the selected color image to the selectedImagesContainer
            selectedImagesContainer.addView(colorImageView);

            // Speak the label based on language settings
            if (speak) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            tts.setLanguage(lanEn ? new Locale("en_EN") : new Locale("cs_CZ"));
                            tts.setSpeechRate(1.0f);
                            if (lanEn && position >= 0 && position < numTTS.length) {
                                tts.speak(numTTS[position], TextToSpeech.QUEUE_FLUSH, null);
                            } else if (!lanEn && position >= 0 && position < colorTTScz.length) {
                                tts.speak(numTTS[position], TextToSpeech.QUEUE_FLUSH, null);
                            } else {
                                // Handle the case where position is out of bounds
                                Log.e("TextToSpeech", "Invalid position: " + position);
                            }
                        }
                    }
                });
            }
        }

    }


    class ImageInfo {
        private final int id;
        private final String label;
        private final int count;
        private final String imageName;

        public ImageInfo(int id, String label, int count, String imageName) {
            this.id = id;
            this.label = label;
            this.count = count;
            this.imageName = imageName;
        }

        public int getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public int getCount() {
            return count;
        }

        public String getImageName() {
            return imageName;
        }
    }

    static class CSVUtils {
        private static final char DEFAULT_SEPARATOR = ',';

        static void writeLine(FileWriter writer, String[] values) throws IOException {
            writeLine(writer, values, DEFAULT_SEPARATOR, ' ');
        }

        private static void writeLine(FileWriter writer, String[] values, char separators, char customQuote) throws IOException {
            boolean first = true;
            // default customQuote is empty
            if (values.length > 0) {
                for (String value : values) {
                    if (!first) {
                        writer.append(separators);
                    }
                    if (customQuote == ' ') {
                        writer.append(followCVSformat(value));
                    } else {
                        writer.append(customQuote).append(followCVSformat(value)).append(customQuote);
                    }
                    first = false;
                }
                writer.append('\n');
            }
        }

        private static String followCVSformat(String value) {
            String result = value;
            if (result.contains("\"")) {
                result = result.replace("\"", "\"\"");
            }
            return result;
        }
    }


    private void loadSettings() {
        try {
            // Read JSON from file
            JSONObject json = readJsonFromFile();
            if (json != null) {
                // Parse JSON into AppSettings
                AppSettings appSettings = AppSettings.fromJson(json);
                if (appSettings != null) {
                    // Apply settings
                    speak = appSettings.isSpeak();
                    lanEn = appSettings.isLanEn();
                    clientRegistered = appSettings.isclRegistered();
                    Userusername = String.valueOf(appSettings.isUserusername());
                    Useremail = String.valueOf(appSettings.isUseremail());
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject readJsonFromFile() throws IOException, JSONException {
        File jsonFile = new File(getExternalFilesDir(null), "app_settings.json");

        FileInputStream fis = null;
        try {
            // Open the file for reading
            fis = new FileInputStream(jsonFile);

            // Read the contents of the file into a string
            StringBuilder stringBuilder = new StringBuilder();
            int content;
            while ((content = fis.read()) != -1) {
                stringBuilder.append((char) content);
            }

            // Convert the string to a JSON object
            return new JSONObject(stringBuilder.toString());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    private void saveSettings() {
        try {
            // Create AppSettings object
            AppSettings appSettings = new AppSettings(Useremail, Userusername, clientRegistered, speak, lanEn);
            // Convert AppSettings to JSON
            JSONObject json = appSettings.toJson();
            // Write JSON to file
            writeJsonToFile(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeJsonToFile(JSONObject json) throws IOException {
        File externalStorageDir = getExternalFilesDir(null);

        if (externalStorageDir != null) {
            File file = new File(externalStorageDir, SETTINGS_FILE);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(json.toString().getBytes());
                Log.d(TAG, "Settings saved to external storage");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.calendar) {
            Toast.makeText(this, "Calendar Selected", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadLabelsFromCSV() {
        imageLables.clear(); // Clear the imageLables array before loading new labels
        File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            // Skip the header line.
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String label = tokens[1].trim();
                    // Add label to the imageLables array
                    imageLables.add(label);
                    System.out.println("Labels: " + label);
                }
            }
            Log.i("Main Activity", "Labels loaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Main Activity", "Error on loading labels");
        }
    }

    private void selectImageFromGallery() {
        try {
            ImagePicker.with(MainActivity.this).cropSquare().compress(128).maxResultSize(500, 500).saveDir(getExternalFilesDir(null) + "/images").start();
        } catch (Exception e) {
            Log.e("Main Activity", "Error on opening Gallery and ImagePicker");
        }


    }

    private void copyImageToRootFolder(Uri sourceUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);

            if (inputStream != null) {
                // Create DCIM directory if it doesn't exist
                File dcimDirectory = new File(getExternalFilesDir(null), "images");
                if (!dcimDirectory.exists()) {
                    dcimDirectory.mkdirs();
                }

                // Find the highest number in the existing image file names
                int maxNumberInFileName = findMaxNumberInFileName(dcimDirectory);

                // Increment the highest number to get the new image file name
                String destinationFileName = "image_" + (maxNumberInFileName + 1) + ".jpg";

                // Construct the destination file path
                destinationFile = new File(dcimDirectory, destinationFileName);
                OutputStream outputStream = new FileOutputStream(destinationFile);
                byte[] buffer = new byte[512];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();

                // Now you can update the CSV file and perform other necessary operations
                showCustomLabelDialog(destinationFile.getAbsolutePath());
                Log.i("Main Activity", "Copy successful");

                adapter.updateData(images, imageLables);
                loadLabelsFromCSV();
                loadImages(imageLables);
                adapter.notifyDataSetChanged();
            } else {
                // Handle the case where inputStream is null
                Log.e("CopyImage", "Input stream is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
            Log.e("CopyImage", "Error occurred while copying image");
        }
    }

    private int findMaxNumberInFileName(File directory) {
        int maxNumber = 0;
        // Get a list of files in the directory
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Extract the numeric part from the file name
                    int number = extractNumberFromFileName(file.getName());
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                }
            }
        }
        return maxNumber;
    }

    private int findMaxId() {
        int maxId = 0;
        for (int id : ids) {
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId;
    }

    private int findMaxNumberInImageName() {
        int maxNumber = 0;
        for (String imageName : imageNames) {
            // Extract the numeric part from the image name
            int number = extractNumberFromImageName(imageName);
            if (number > maxNumber) {
                maxNumber = number;
            }
        }
        return maxNumber;
    }

    private int extractNumberFromFileName(String fileName) {
        try {
            // Extract the numeric part from the file name
            String numericPart = fileName.replaceAll("[^0-9]", "");
            // Convert the numeric part to an integer
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            // Handle the case where the numeric part cannot be parsed as an integer
            e.printStackTrace();
            // Return a default value or throw a custom exception as needed
            return -1; // Default value indicating failure
        }
    }

    private void deleteNonStandardImages() {
        // Create DCIM directory if it doesn't exist
        File dcimDirectory = new File(getExternalFilesDir(null), "images");
        if (dcimDirectory.exists() && dcimDirectory.isDirectory()) {
            File[] files = dcimDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        // Check if the file name matches the desired pattern "image_X.jpg"
                        if (!file.getName().matches("image_\\d+\\.jpg")) {
                            // Delete the file if the name doesn't match the pattern
                            if (file.delete()) {
                                Log.i("Main Activity", "Deleted non-standard image: " + file.getName());
                            } else {
                                Log.e("Main Activity", "Failed to delete non-standard image: " + file.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    private void showCustomLabelDialog(String absolutePath) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        builder.setView(dialogView);
        ImageView selectedImageContainerPreview = dialogView.findViewById(R.id.selectedImageContainerPreview);


        Log.e(TAG, "absolutePath: " + absolutePath);
        Log.e(TAG, "lastImageUri(): " + lastImageUri());


        if (lastImageUri() != null && !lastImageUri().isEmpty()) {
            File imgFile = new File(lastImageUri());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                selectedImageContainerPreview.setImageBitmap(myBitmap);
            }
        } else {
            Toast.makeText(this, "URI is empty or null", Toast.LENGTH_SHORT).show();
        }


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText input = dialogView.findViewById(R.id.editText);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        if (lanEn) {
            positiveButton.setText(en[13]);
        } else {
            positiveButton.setText(cz[13]);

        }


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button negativeButton = dialogView.findViewById(R.id.negativeButton);
        if (lanEn) {
            negativeButton.setText(en[6]);
        } else {
            negativeButton.setText(cz[6]);

        }


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button nextPdfButton = dialogView.findViewById(R.id.neutralButton1);
        if (lanEn) {
            nextPdfButton.setText(en[14]);
        } else {
            nextPdfButton.setText(cz[14]);

        }


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button nextImageButton = dialogView.findViewById(R.id.neutralButton2);
        if (lanEn) {
            nextImageButton.setText(en[15]);
        } else {
            nextImageButton.setText(cz[15]);

        }


        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customLabel = input.getText().toString().trim().replaceAll("[^\\p{L}0-9\\s]", "");

                if (!TextUtils.isEmpty(customLabel)) {
                    // Capitalize the first letter of the label
                    customLabel = capitalizeFirstLetter(customLabel);

                    // Check if the custom label is not included in the current set of imageLabels
                    if (!imageLables.contains(customLabel)) {
                        // Call the updateCSVAndOtherOperations method with the custom label
                        updateCSVAndOtherOperations(absolutePath, customLabel);
                        imageLables.add(customLabel);  // Now the array is properly initialized
                        Log.i("Main Activity", "Array ImageLabel: " + imageLables);
                        adapter.updateData(images, imageLables);
                        loadLabelsFromCSV();
                        loadImages(imageLables);
                        adapter.notifyDataSetChanged();
                        // Close the dialog
                        dialog.dismiss();
                    } else {
                        // Show a toast indicating that the image cannot be added due to an invalid label
                        Toast.makeText(MainActivity.this, "Image cannot be added because of same or invalid label", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, StringEmptyLabel, Toast.LENGTH_SHORT).show();
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLastAddedImage();
                deleteNonStandardImages();
                dialog.dismiss();
            }
        });

        nextPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customLabel = input.getText().toString().trim();
                if (!TextUtils.isEmpty(customLabel)) {
                    // Capitalize the first letter of the label
                    customLabel = capitalizeFirstLetter(customLabel);

                    // Check if the custom label is not included in the current set of imageLabels
                    if (!imageLables.contains(customLabel)) {
                        // Call the updateCSVAndOtherOperations method with the custom label
                        updateCSVAndOtherOperations(absolutePath, customLabel);
                        imageLables.add(customLabel);  // Now the array is properly initialized
                        Log.i("Main Activity", "Array ImageLabel: " + imageLables);
                        adapter.updateData(images, imageLables);
                        loadLabelsFromCSV();
                        loadImages(imageLables);
                        adapter.notifyDataSetChanged();
                        // Close the dialog
                        dialog.dismiss();

                        pickPdfFile();

                    } else {
                        // Show a toast indicating that the image cannot be added due to an invalid label
                        Toast.makeText(MainActivity.this, "Image cannot be added because of same or invalid label", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, StringEmptyLabel, Toast.LENGTH_SHORT).show();
                }
            }
        });

        nextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customLabel = input.getText().toString().trim();
                if (!TextUtils.isEmpty(customLabel)) {
                    // Capitalize the first letter of the label
                    customLabel = capitalizeFirstLetter(customLabel);

                    // Check if the custom label is not included in the current set of imageLabels
                    if (!imageLables.contains(customLabel)) {
                        // Call the updateCSVAndOtherOperations method with the custom label
                        updateCSVAndOtherOperations(absolutePath, customLabel);
                        imageLables.add(customLabel);  // Now the array is properly initialized
                        Log.i("Main Activity", "Array ImageLabel: " + imageLables);
                        adapter.updateData(images, imageLables);
                        loadLabelsFromCSV();
                        loadImages(imageLables);
                        adapter.notifyDataSetChanged();
                        // Close the dialog
                        dialog.dismiss();

                        selectImageFromGallery();

                    } else {
                        // Show a toast indicating that the image cannot be added due to an invalid label
                        Toast.makeText(MainActivity.this, "Image cannot be added because of same or invalid label", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, StringEmptyLabel, Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog = builder.create();
        dialog.setCancelable(false); // Prevent dialog dismissal on outside touch
        dialog.show();
    }

    private String lastImageUri() {

        pathToLastAddedImage = null;
        // Create DCIM directory if it doesn't exist
        File dcimDirectory = new File(getExternalFilesDir(null), "images");
        if (dcimDirectory.exists() && dcimDirectory.isDirectory()) {
            // Get a list of all files in the directory
            File[] files = dcimDirectory.listFiles();
            if (files != null && files.length > 0) {
                // Sort the files by name (ascending order)
                Arrays.sort(files, Comparator.comparing(File::getName));

                // Find the file with the highest number in the name
                File fileWithHighestNumber = null;
                int highestNumber = Integer.MIN_VALUE;
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        if (fileName.startsWith("image_") && fileName.endsWith(".jpg")) {
                            try {
                                int number = Integer.parseInt(fileName.substring(6, fileName.length() - 4));
                                if (number > highestNumber) {
                                    highestNumber = number;
                                    fileWithHighestNumber = file;
                                    pathToLastAddedImage = String.valueOf(fileWithHighestNumber);
                                }
                            } catch (NumberFormatException e) {
                                // Handle the case where the file name does not contain a valid number
                                e.printStackTrace();
                                Toast.makeText(this, "Invalid Number in Name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "No images found in directory", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Directory does not exist", Toast.LENGTH_SHORT).show();
        }
        return pathToLastAddedImage;
    }


    private void removeLastAddedImage() {
        // Create DCIM directory if it doesn't exist
        File dcimDirectory = new File(getExternalFilesDir(null), "images");
        if (dcimDirectory.exists() && dcimDirectory.isDirectory()) {
            // Get a list of all files in the directory
            File[] files = dcimDirectory.listFiles();
            if (files != null && files.length > 0) {
                // Sort the files by name (ascending order)
                Arrays.sort(files, Comparator.comparing(File::getName));

                // Find the file with the highest number in the name
                File fileWithHighestNumber = null;
                int highestNumber = Integer.MIN_VALUE;
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        if (fileName.startsWith("image_") && fileName.endsWith(".jpg")) {
                            try {
                                int number = Integer.parseInt(fileName.substring(6, fileName.length() - 4));
                                if (number > highestNumber) {
                                    highestNumber = number;
                                    fileWithHighestNumber = file;
                                }
                            } catch (NumberFormatException e) {
                                // Handle the case where the file name does not contain a valid number
                                e.printStackTrace();
                                Toast.makeText(this, "Invalid Number in Name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                // Delete the file with the highest number, if found
                if (fileWithHighestNumber != null) {
                    boolean deleted = fileWithHighestNumber.delete();
                    if (deleted) {
                    } else {
                        Toast.makeText(this, "Failed to delete last image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No image found to delete", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No images found in directory", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Directory does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    private String capitalizeFirstLetter(String label) {
        if (label.length() > 0) {
            return Character.toUpperCase(label.charAt(0)) + label.substring(1);
        } else {
            return label;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateCSVAndOtherOperations(String imagePath, String customLabel) {
        try {
            File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");

            // Find the highest ID and highest number in image names
            int maxId = findMaxId();
            int maxNumberInImageName = findMaxNumberInImageName();

            // Increment the max ID and highest number in image names for the new image
            int newId = maxId + 1;
            int newNumberInImageName = maxNumberInImageName + 1;

            // Construct the new image name using the incremented number
            String imageName = "image_" + newNumberInImageName + ".jpg";

            // Update the CSV file with the new image entry
            updateCSVWithNewImageEntry(csvFile, newId, customLabel, newNumberInImageName, imageName);

            // After updating the CSV, trigger necessary operations
            readCSV();
            loadLabelsFromCSV();
            ArrayList<String> imageLabels = new ArrayList<>();
            loadImages(imageLabels);
            sortCSVByCount();
            sortImagesByCount();
            deleteNonStandardImages();


            // Update the adapter with the new data
            adapter.updateData(images, imageLabels);

            clearGlideDiskCache();

            adapter.notifyDataSetChanged();


        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
            Log.e("UpdateCSV", "Error occurred while updating CSV");
        }
    }

    private int extractNumberFromImageName(String imageName) {
        // Extract the numeric part from the image name
        String numericPart = imageName.replaceAll("[^0-9]", "");
        // Convert the numeric part to an integer
        return Integer.parseInt(numericPart);
    }

    private void clearGlideDiskCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(getApplicationContext()).clearDiskCache();
            }
        }).start();
    }

    private void updateCSVWithNewImageEntry(File csvFile, int newId, String customLabel, int newCount, String imageName) throws IOException {
        // Create a FileWriter with append mode
        FileWriter writer = new FileWriter(csvFile, true);

        // Create a CSVWriter with the FileWriter, disabling quote characters
        CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        // Add a new line with the image path, custom label, id, and count
        String[] data = {String.valueOf(newId), customLabel, String.valueOf(newCount), imageName};
        csvWriter.writeNext(data);

        // Flush and close the CSVWriter
        csvWriter.flush();
        csvWriter.close();
    }

    private void createEmptyCSV() {

        File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");

        try {
            if (!csvFile.exists()) {
                // Create the CSV file if it doesn't exist
                csvFile.createNewFile();

                // Initialize the CSV file with the header
                FileWriter writer = new FileWriter(csvFile, true);
                writer.append("id,label,count,imageName\n");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Main Activity", " Filed to make and CSV");
        }
    }

    private void updateCSV(int position) {
        if (counts != null && position >= 0 && position < counts.length) {
            // Increment the "count" value for the corresponding image
            counts[position]++;

            // Update the CSV file with the new counts
            writeCSV(position);

            // Update the counts array
            updateCountsArray();

            // Notify the adapter that the data has changed
            loadLabelsFromCSV();
            loadImages(imageLables);
        }
    }

    private void updateCountsArray() {
        for (int i = 0; i < dataCount; i++) {
            counts[i] = getCountFromCSV(i);
        }
    }

    private int getCountFromCSV(int position) {
        File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");
        int count = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;

            // Skip the header line.
            reader.readLine();

            int currentLine = 0;

            while ((line = reader.readLine()) != null) {
                if (currentLine == position) {
                    String[] tokens = line.split(",");
                    if (tokens.length >= 3) {
                        count = Integer.parseInt(tokens[2].trim());
                    }
                    break;
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    private void writeCSV(int position) {
        StringBuilder csvData = new StringBuilder("id,label,count,imageName\n");

        for (int i = 0; i < ids.length; i++) {
            csvData.append(ids[i]).append(",");
            csvData.append(labels[i]).append(",");
            csvData.append(counts[i]).append((","));
            csvData.append(imageNames[i]).append("\n");
        }

        // Now, write the updated CSV data back to the file
        try {
            File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");
            boolean isNewFile = false;

            if (!csvFile.exists()) {
                isNewFile = true;
                csvFile.createNewFile();
            }

            FileWriter writer = new FileWriter(csvFile, false);
            writer.write(csvData.toString());
            writer.flush();
            writer.close();

            if (isNewFile) {
                // Set appropriate permissions for the new file
                csvFile.setReadable(true);
                csvFile.setWritable(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readCSV() {
        ids = new int[0];
        labels = new String[0];
        counts = new int[0];
        imageNames = new String[0];
        dataCount = 0;

        createEmptyCSV();

        File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");
        if (csvFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(csvFile));
                String line;

                // Skip the header line.
                reader.readLine();

                List<Integer> idList = new ArrayList<>();
                List<String> labelList = new ArrayList<>();
                List<Integer> countList = new ArrayList<>();
                List<String> imageNameList = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length >= 3) {
                        int id = Integer.parseInt(tokens[0].trim());
                        String label = tokens[1].trim();
                        int count = Integer.parseInt(tokens[2].trim());
                        String imageName = tokens[3].trim();

                        idList.add(id);
                        labelList.add(label);
                        countList.add(count);
                        imageNameList.add(imageName);
                    }
                }

                // Convert ArrayLists to arrays
                ids = idList.stream().mapToInt(Integer::intValue).toArray();
                labels = labelList.toArray(new String[0]);
                counts = countList.stream().mapToInt(Integer::intValue).toArray();
                imageNames = imageNameList.toArray(new String[0]);
                dataCount = ids.length;

                Log.i("Main Activity", "CSV read successful");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadImages(ArrayList<String> imageLabels) {
        images.clear();

        File imagesDirectory = new File(getExternalFilesDir(null), "/images");

        if (imagesDirectory.exists() && imagesDirectory.isDirectory()) {
            // The directory exists, so we can proceed to load images.

            // Create a list to store ImageInfo objects

            List<ImageInfo> imageInfoList = new ArrayList<>();

            for (int i = 0; i < dataCount; i++) {
                ImageInfo imageInfo = new ImageInfo(ids[i], labels[i], counts[i], imageNames[i]);
                imageInfoList.add(imageInfo);
            }


            // Update arrays based on the sorted list
            for (int i = 0; i < imageInfoList.size(); i++) {
                ImageInfo imageInfo = imageInfoList.get(i);
                ids[i] = imageInfo.getId();
                labels[i] = imageInfo.getLabel();
                counts[i] = imageInfo.getCount();
                imageNames[i] = imageInfo.getImageName();

                // Add the sorted image paths to the 'images' list
                File sortedImageFile = new File(imagesDirectory, imageInfo.getImageName());
                images.add(sortedImageFile.getAbsolutePath());

                // Retrieve image label based on file name and add it to imageLabels
                String imageName = sortedImageFile.getName();
                String label = imageLabelMap.get(imageName);
                getImageLabelFromCSV(imageName);
                if (label != null) {
                    imageLabels.add(label);
                    adapter.addImageLabel(label);
                } else {

                }
            }
        }
    }

    private void sortImagesByCount() {

        readCSV();
        List<ImageInfo> imageInfoList = new ArrayList<>();
        for (int i = 0; i < dataCount; i++) {
            ImageInfo imageInfo = new ImageInfo(ids[i], labels[i], counts[i], imageNames[i]);
            imageInfoList.add(imageInfo);
        }

        // Sort the list based on counts
        Collections.sort(imageInfoList, new Comparator<ImageInfo>() {
            @Override
            public int compare(ImageInfo o1, ImageInfo o2) {
                return Integer.compare(o2.getCount(), o1.getCount());
            }
        });

        // Clear existing data
        ids = new int[imageInfoList.size()];
        labels = new String[imageInfoList.size()];
        counts = new int[imageInfoList.size()];
        imageNames = new String[imageInfoList.size()];

        // Populate arrays with sorted data
        for (int i = 0; i < imageInfoList.size(); i++) {
            ImageInfo imageInfo = imageInfoList.get(i);
            ids[i] = imageInfo.getId();
            labels[i] = imageInfo.getLabel();
            counts[i] = imageInfo.getCount();
            imageNames[i] = imageInfo.getImageName();
        }

        Log.i("Main Activity", "Images sorted");

        // Clear the selected images container
        selectedImagesContainer.removeAllViews();
        selectedImages.clear();

        // Introduce a delay before reloading images
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Load images on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadImages(new ArrayList<>());
                    }
                });
            }
        }, 100); // Adjust the delay duration as needed
    }

    private void sortCSVByCount() {
        readCSV();

        // Create a list of ImageInfo objects to store image information
        List<ImageInfo> imageInfoList = new ArrayList<>();

        // Populate the list with image information
        for (int i = 0; i < dataCount; i++) {
            ImageInfo imageInfo = new ImageInfo(ids[i], labels[i], counts[i], imageNames[i]);
            imageInfoList.add(imageInfo);
        }

        // Sort the list based on 'count' value
        Collections.sort(imageInfoList, new Comparator<ImageInfo>() {
            @Override
            public int compare(ImageInfo o1, ImageInfo o2) {
                // Compare based on 'count' in descending order
                return Integer.compare(o2.getCount(), o1.getCount());
            }
        });

        // Clear existing data
        ids = new int[imageInfoList.size()];
        labels = new String[imageInfoList.size()];
        counts = new int[imageInfoList.size()];
        imageNames = new String[imageInfoList.size()];

        // Populate arrays with sorted data
        for (int i = 0; i < imageInfoList.size(); i++) {
            ImageInfo imageInfo = imageInfoList.get(i);
            ids[i] = imageInfo.getId();
            labels[i] = imageInfo.getLabel();
            counts[i] = (imageInfoList.size() - i);
            imageNames[i] = imageInfo.getImageName();
        }

        // Write the sorted data back to the CSV file
        writeCSVToFile();

        // Reload labels in the correct order
        loadLabelsFromCSV();

        // Notify the adapter that the data has changed
        adapter.updateData(images, imageLables);
        loadLabelsFromCSV();
        loadImages(imageLables);
        adapter.notifyDataSetChanged();

        Log.i("Main Activity", "CSV sorted");
    }

    private void writeCSVToFile() {
        try {
            File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");

            // Create a FileWriter with append mode
            FileWriter writer = new FileWriter(csvFile, false);

            // Write the header
            writer.append("id,label,count,imageName\n");

            // Write the data
            for (int i = 0; i < ids.length; i++) {
                String[] data = {String.valueOf(ids[i]), labels[i], String.valueOf(counts[i]), imageNames[i]};
                CSVUtils.writeLine(writer, data);
            }

            // Flush and close the FileWriter
            writer.flush();
            writer.close();

            Log.i("Main Activity", "CSV file written successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Main Activity", "Error occurred while writing CSV file");
        }
    }

    private String getImageLabelFromCSV(String imageName) {
        File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;

            // Skip the header line.
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String csvImageName = tokens[3].trim();
                    String label = tokens[1].trim();

                    // Check if the image names match
                    if (csvImageName.equals(imageName)) {
                        return label;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Return null if label not found
    }

    private void entsureImagesFolderExists() {
        File dcimDirectory = new File(getFilesDir(), "images");
        if (!dcimDirectory.exists()) {
            dcimDirectory.mkdirs();
        }
    }


    private void checkPermissionsRead() {
        ArrayList<String> imageLabels = new ArrayList<>(); // Declare the imageLabels ArrayList
        int resultR = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int resultW = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        if (resultR == PackageManager.PERMISSION_GRANTED && resultW == PackageManager.PERMISSION_GRANTED) {
            readCSV();

            loadImages(imageLabels);
            Log.d("My Activity", "IMAGES LOADED!!!!!!");
            System.out.println("CHECK_PERMISSIONS and pls be goood");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, READ_MEDIA_IMAGES, WRITE_EXTERNAL_STORAGE

            }, PERMISSION_REQUEST_CODE);
            Log.d("My Activity", "REQUESTED");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> imageLabels = new ArrayList<>(); // Declare the imageLabels ArrayList

        if (grantResults.length > 0) {
            boolean accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (accepted) {
                readCSV();

                loadImages(imageLabels);

                System.out.println("ACCEPTED ALL, GOOD");
            } else {
                Toast.makeText(this, en[8], Toast.LENGTH_LONG).show();
            }
        } else {
        }
    }


    @Override
    public void onItemClick(int position) {

        if (images != null && position >= 0 && position < images.size() && displayImages && selectedImages.size() + ColNumSel <= ColumnsNumber - 1) {
            String imagePath = images.get(position);
            MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.pop_sound);
            mp.setVolume(lvol, rvol);
            mp.start();

            // Calculate the maximum number of images that can fit based on the layout width
            if (isTablet()) {
                desiredImageWidthDP = 200;
            } else {
                desiredImageWidthDP = 120;
            }


            // Convert dp to pixels
            float density = getResources().getDisplayMetrics().density;
            int imageWidth = (int) (desiredImageWidthDP * density);

            int containerWidth = selectedImagesContainer.getWidth();
            int maxImages = (containerWidth / imageWidth);

            displayImages = selectedImages.size() <= maxImages;


            if (displayImages) {

                if (isDividedLayout) {

                    // Handle divided layout here
                    String selectedDay = "";
                    RadioGroup radioGroup = findViewById(R.id.dayGroup);
                    switch (SelectedDay) {
                        case 0:
                            selectedDay = "Monday";
                            Toast.makeText(this, "Added image to: " + selectedDay, Toast.LENGTH_SHORT).show();
                            mondayImages.add(imagePath);
                            System.out.print(mondayImages);
                            Log.e("Main Activity", selectedDay + " " + String.valueOf(mondayImages.size()));
                            displayImagesByDay(SelectedDay);


                            break;
                        case 1:
                            selectedDay = "Tuesday";
                            Toast.makeText(this, "Added image to: " + selectedDay, Toast.LENGTH_SHORT).show();
                            tuesdayImages.add(imagePath);
                            System.out.print(tuesdayImages);
                            Log.e("Main Activity", selectedDay + " " + String.valueOf(tuesdayImages.size()));
                            displayImagesByDay(SelectedDay);


                            break;
                        case 2:
                            selectedDay = "Wednesday";
                            Toast.makeText(this, "Added image to: " + selectedDay, Toast.LENGTH_SHORT).show();
                            wednesdayImages.add(imagePath);
                            System.out.print(wednesdayImages);
                            Log.e("Main Activity", selectedDay + " " + String.valueOf(wednesdayImages.size()));
                            displayImagesByDay(SelectedDay);


                            break;
                        case 3:
                            selectedDay = "Thursday";
                            Toast.makeText(this, "Added image to: " + selectedDay, Toast.LENGTH_SHORT).show();
                            thursdayImages.add(imagePath);
                            System.out.print(thursdayImages);
                            Log.e("Main Activity", selectedDay + " " + String.valueOf(thursdayImages.size()));
                            displayImagesByDay(SelectedDay);


                            break;
                        case 4:
                            selectedDay = "Friday";
                            Toast.makeText(this, "Added image to: " + selectedDay, Toast.LENGTH_SHORT).show();
                            fridayImages.add(imagePath);
                            System.out.print(fridayImages);
                            Log.e("Main Activity", selectedDay + " " + String.valueOf(fridayImages.size()));
                            displayImagesByDay(SelectedDay);


                            break;
                        case 5:
                            selectedDay = "Saturday";
                            Toast.makeText(this, "Added image to: " + selectedDay, Toast.LENGTH_SHORT).show();
                            saturdayImages.add(imagePath);
                            System.out.print(saturdayImages);
                            Log.e("Main Activity", selectedDay + " " + String.valueOf(saturdayImages.size()));
                            displayImagesByDay(SelectedDay);


                            break;
                        case 6:
                            selectedDay = "Sunday";
                            Toast.makeText(this, "Added image to: " + selectedDay, Toast.LENGTH_SHORT).show();
                            sundayImages.add(imagePath);
                            System.out.print(sundayImages);
                            Log.e("Main Activity", selectedDay + " " + String.valueOf(sundayImages.size()));
                            displayImagesByDay(SelectedDay);


                            break;
                    }


                    // Add the clicked image to the corresponding day array
                    if (!selectedDay.isEmpty()) {
                        List<String> dayArray = dayImages.get(selectedDay);
                        if (dayArray != null) {
                            String imageDayPath = images.get(position);
                            if (!dayArray.contains(imageDayPath)) {
                                dayArray.add(imageDayPath);
                                // Notify adapter or update UI as needed
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }


                } else {
                    if (!selectedImages.contains(imagePath)) {
                        selectedImages.add(imagePath);

                        // Update 'count' value and sort images
                        updateCSV(position);


                        ImageView selectedImageView = new ImageView(this);

                        if (isTablet()) {
                            dpWidth = 200;
                            dpHeight = 200;
                        } else {
                            dpWidth = 120;
                            dpHeight = 120;
                        }


                        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, getResources().getDisplayMetrics());
                        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, getResources().getDisplayMetrics());

                        Glide.with(this).load(imagePath).centerCrop().override(width, height) // Set the size of the image view
                                .into(selectedImageView);

                        // Create a FrameLayout to control the spacing
                        FrameLayout frameLayout = new FrameLayout(this);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(15, 0, 15, 0); // Add left and right margins here
                        selectedImageView.setLayoutParams(layoutParams);
                        frameLayout.addView(selectedImageView, layoutParams);
                        selectedImagesContainer.addView(frameLayout);


                        if (!lanEn) {
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (speak) {
                                        if (status == TextToSpeech.SUCCESS) {
                                            tts.setLanguage(new Locale("cs_CZ"));
                                            tts.setSpeechRate(1.0f);
                                            if (position >= 0 && position < labels.length) {
                                                tts.speak(labels[position], TextToSpeech.QUEUE_FLUSH, null);
                                            } else {
                                                // Handle the case where position is out of bounds
                                                Log.e("TextToSpeech", "Invalid position: " + position);
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (speak) {
                                        if (status == TextToSpeech.SUCCESS) {
                                            tts.setLanguage(new Locale("en_CZ"));
                                            tts.setSpeechRate(1.0f);
                                            if (position >= 0 && position < labels.length) {
                                                tts.speak(labels[position], TextToSpeech.QUEUE_FLUSH, null);
                                            } else {
                                                // Handle the case where position is out of bounds
                                                Log.e("TextToSpeech", "Invalid position: " + position);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        Log.d("Main Activity", "Image " + position + " displayed");
                    }
                }
            } else {
                Toast.makeText(this, StringMaxImages + maxImages, Toast.LENGTH_SHORT).show();
            }
        }


    }

    private GalleryAdapter.OnItemLongClickListener itemLongClickListener = new GalleryAdapter.OnItemLongClickListener() {
        @Override
        public void onItemLongClick(int position) {
            MainActivity.this.onItemLongClick(position);
        }
    };
    private GalleryAdapter.OnItemClickListener itemClickListener = new GalleryAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            MainActivity.this.onItemClick(position);
        }

        @Override
        public void onItemLongClick(int position) {
        }
    };


    public void onItemLongClick(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (lanEn) {
            builder.setMessage(en[16]).setPositiveButton(en[17], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteImage(position, MainActivity.this);
                    Toast.makeText(MainActivity.this, en[17] + imageLables.get(position) + en[18], Toast.LENGTH_SHORT).show();

                }
            }).setNegativeButton(en[18], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog, do nothing
                }
            });
            // Create the AlertDialog object and return it
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            builder.setMessage(cz[16]).setPositiveButton(cz[17], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteImage(position, MainActivity.this);
                    Toast.makeText(MainActivity.this, cz[17] + imageLables.get(position) + cz[18], Toast.LENGTH_SHORT).show();

                }
            }).setNegativeButton(cz[18], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog, do nothing
                }
            });
            // Create the AlertDialog object and return it
            AlertDialog dialog = builder.create();
            dialog.show();
        }


    }

    public void registerClient() {
        showRegistrationWindow();
    }

    private void showRegistrationWindow() {
        // Inflate the registration layout
        View registrationView = LayoutInflater.from(this).inflate(R.layout.registration_window, null);

        // Find the EditText fields
        EditText editTextEmail = registrationView.findViewById(R.id.editTextEmail);
        EditText editTextUsername = registrationView.findViewById(R.id.editTextUsername);

        // Add any necessary listeners to registration elements
        Button submitButton = registrationView.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user inputs
                Useremail = editTextEmail.getText().toString();
                Userusername = editTextUsername.getText().toString();

                // Validate and handle the user inputs
                if (isValidInput(Useremail, Userusername)) {

                    // Dismiss the registration window
                    registrationDialog.dismiss();
                    clientRegistered = true;
                    saveSettings(); // Save updated settings


                } else {
                    // Display an error message or handle invalid input
                    Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Show the registration dialog/window
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(registrationView);
        builder.setCancelable(false); // Prevent dismissing by clicking outside
        registrationDialog = builder.create();
        registrationDialog.show();
    }

    private boolean isValidInput(String email, String username) {
        // Implement your validation logic here
        boolean isEmailValid = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        // Check if username is not empty
        boolean isUsernameValid = !TextUtils.isEmpty(username);
        // Return true if input is valid, false otherwise
        return isEmailValid && isUsernameValid;
    }


    private void addBundleTrans() {
        // Create an intent to open the document tree
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");

        // Start the activity for result
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }


    private static final int PICK_PDF_REQUEST = 123;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == ImagePicker.REQUEST_CODE) {
                // Handle selected images from ImagePicker library
                handleImagePickerResult(data.getData());
                removeImagesFromRootFolder();
            } else if (requestCode == PICK_PDF_REQUEST) {
                // Handle selected PDF file
                handleSelectedPdf(data);
                removeImagesFromRootFolder();
            } else if (requestCode == UCrop.REQUEST_CROP) {
                // Handle cropped image
                handleCroppedImage(data);
                removeImagesFromRootFolder();
            } else {
                // Check if the selected file is a ZIP file
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    String fileName = getFileNameFromUri(fileUri);
                    if (fileName != null && fileName.toLowerCase().endsWith(".zip")) {
                        // Selected file is a ZIP file
                        handleSelectedZip(fileUri);
                    } else {
                        // Selected file is not a ZIP file
                        Toast.makeText(this, "Please select a ZIP file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    private void handleImagePickerResult(Uri uri) {
        copyImageToRootFolder(uri);
    }

    private void handleSelectedPdf(Intent data) {
        Uri pdfUri = data.getData();
        if (pdfUri != null) {
            // Extract images from the PDF and display them for cropping
            extractImagesFromPDF(pdfUri);
        }
    }

    private void handleCroppedImage(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null) {
            try {
                // Open input stream from the cropped image
                InputStream inputStream = getContentResolver().openInputStream(resultUri);
                copyImageToRootFolder(resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Uri is null");
        }
    }

    private void handleSelectedZip(Uri data) {
        Toast.makeText(this, "Select zip called", Toast.LENGTH_SHORT).show();
        Uri zipUri = data;
        if (zipUri != null) {
            // Copy the ZIP file to the app's private storage and extract its contents
            copyZipFileToAppsFolder(zipUri);
        } else {
            Toast.makeText(this, "Bad Uri", Toast.LENGTH_SHORT).show();
        }
    }


    private void copyZipFileToAppsFolder(Uri uri) {
        Toast.makeText(this, "Copy of the zip started", Toast.LENGTH_SHORT).show();
        try {
            // Show loading dialog


            // Get input stream for the ZIP file
            InputStream inputStream = getContentResolver().openInputStream(uri);

            // Extract the file name from the URI
            String fileName = getFileNameFromUri(uri);
            Log.d(TAG, "This is Name Of an .zip: " + fileName);

            // Create a file for the ZIP file in the app's private storage
            File appPrivateDir = getExternalFilesDir(null);
            File zipFile = new File(appPrivateDir, fileName);

            // Copy the ZIP file to the app's private storage
            OutputStream outputStream = new FileOutputStream(zipFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();

            Log.d("YourActivity", "Bundle.zip file copied to app's private storage");

            // Now the bundle.zip file is copied to the app's private storage
            // You can proceed with extracting its contents

            // Implement the logic to extract the contents of bundle.zip
            loadBundleFromZip(uri);
            copyImagesFromBundleZip(uri);
            sortImagesByCount();
            adapter.updateData(images, imageLables);
            loadLabelsFromCSV();
            loadImages(imageLables);
            adapter.notifyDataSetChanged();


        } catch (IOException e) {
            e.printStackTrace();
            Log.e("YourActivity", "Error copying bundle.zip file: " + e.getMessage());
            Toast.makeText(this, "Copy ZIP failed", Toast.LENGTH_SHORT).show();

        }
    }

    // Helper method to extract the file name from the URI
    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (fileName == null) {
            fileName = "bundle.zip"; // Default name if unable to retrieve from URI
        }
        return fileName;
    }

    private void loadBundleFromZip(Uri zipUri) {
        try {
            // Open the zip file for reading
            ZipInputStream zipInputStream = new ZipInputStream(getContentResolver().openInputStream(zipUri));
            ZipEntry entry;

            // Look for the bundle.csv file within the zip archive
            while ((entry = zipInputStream.getNextEntry()) != null) {


                if (entry.getName().equals("Bundle/bundle.csv")) {
                    // If bundle.csv is found, load its contents into arrays
                    List<Integer> bundleIdList = new ArrayList<>();
                    List<String> bundleLabelList = new ArrayList<>();
                    List<Integer> bundleCountList = new ArrayList<>();
                    List<String> bundleImageNameList = new ArrayList<>();

                    // Read the CSV file line by line
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    String line;
                    boolean headerSkipped = false;
                    while ((line = reader.readLine()) != null) {
                        if (!headerSkipped) {
                            // Skip the header line
                            headerSkipped = true;
                            continue;
                        }
                        String[] parts = line.split(",");
                        if (parts.length >= 4) {
                            int id = Integer.parseInt(parts[0].trim());
                            String label = parts[1].trim();
                            int count = Integer.parseInt(parts[2].trim());
                            String imageName = parts[3].trim();

                            // Add data to lists
                            bundleIdList.add(id);
                            bundleLabelList.add(label);
                            bundleCountList.add(count);
                            bundleImageNameList.add(imageName);
                        }
                    }
                    reader.close();

                    // Append bundle data to existing arrays or create new arrays if needed
                    appendBundleDataToExistingArrays(bundleIdList, bundleLabelList, bundleCountList, bundleImageNameList);

                    // Write the combined data back to csvdata.csv
                    writeCSVToFile();

                    // Close the zip input stream and break the loop
                    zipInputStream.closeEntry();
                    break;
                }
            }
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("YourActivity", "Error loading bundle.csv from zip: " + e.getMessage());
            Toast.makeText(this, "Error loading bundle.csv from zip", Toast.LENGTH_SHORT).show();
        }
    }

    private void appendBundleDataToExistingArrays(List<Integer> bundleIds, List<String> bundleLabels, List<Integer> bundleCounts, List<String> bundleImageNames) {
        // Find the maximum existing id value
        int maxId = 0;
        for (int id : ids) {
            maxId = Math.max(maxId, id);
        }

        // Increment id values from bundle.csv to continue from the maximum existing id value
        int nextId = maxId + 1;
        int bundleSize = bundleIds.size();

        // Create new arrays with combined sizes
        int[] newIds = new int[ids.length + bundleSize];
        String[] newLabels = new String[labels.length + bundleSize];
        int[] newCounts = new int[counts.length + bundleSize];
        String[] newImageNames = new String[imageNames.length + bundleSize];

        // Copy existing data to new arrays
        System.arraycopy(ids, 0, newIds, 0, ids.length);
        System.arraycopy(labels, 0, newLabels, 0, labels.length);
        System.arraycopy(counts, 0, newCounts, 0, counts.length);
        System.arraycopy(imageNames, 0, newImageNames, 0, imageNames.length);

        // Append bundle data to new arrays
        for (int i = 0; i < bundleSize; i++) {
            newIds[ids.length + i] = bundleIds.get(i) + maxId; // Use the maximum existing id value
            newLabels[labels.length + i] = bundleLabels.get(i);
            newCounts[counts.length + i] = bundleCounts.get(i);
            newImageNames[ids.length + i] = bundleImageNames.get(i); // Ensure image names are aligned with IDs
        }

        // Update existing arrays with new data
        ids = newIds;
        labels = newLabels;
        counts = newCounts;
        imageNames = newImageNames;
    }

    private void copyImagesFromBundleZip(Uri zipUri) {
        ZipInputStream zipInputStream = null;
        OutputStream outputStream = null;
        try {
            // Open the zip file for reading
            zipInputStream = new ZipInputStream(getContentResolver().openInputStream(zipUri));
            ZipEntry entry;

            // Define the destination directory for images
            File destinationDirectory = new File(getExternalFilesDir(null), "images");
            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdirs(); // Create the directory if it doesn't exist
            }

            // Look for image files within the "Bundle" directory in the zip archive
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().startsWith("Bundle") && isImageFile(entry.getName())) {
                    // Extract the image file name
                    String imageName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);

                    // Create the destination file
                    File destinationFile = new File(destinationDirectory, imageName);

                    // Copy the image file from zip to destination directory
                    outputStream = new FileOutputStream(destinationFile);
                    byte[] buffer = new byte[512];
                    int length;
                    while ((length = zipInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.close();
                    zipInputStream.closeEntry();
                }
            }
            // Log success message
            Log.d(TAG, "Images copied successfully to: " + destinationDirectory.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error copying images from bundle.zip: " + e.getMessage());
            Toast.makeText(this, "Error copying images from bundle.zip", Toast.LENGTH_SHORT).show();


        } finally {
            // Close streams if opened
            try {
                if (zipInputStream != null) {
                    zipInputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isImageFile(String fileName) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        for (String extension : imageExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".bmp");
    }

    public void exportBundle() {
        try {
            // Get the current date in the desired format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());

            // Prepare the filename and content values for the ZIP file
            String zipFileName = "Bundle_" + Userusername + "_" + currentDate + ".zip";
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, zipFileName);
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/zip");
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            // Insert the content into the MediaStore, getting back a URI to the file
            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

            if (uri == null) {
                throw new IOException("Failed to create the file in the Downloads folder.");
            }

            // Open an output stream to write the ZIP file
            try (OutputStream fos = getContentResolver().openOutputStream(uri); ZipOutputStream zos = new ZipOutputStream(fos)) {

                // Get the external files directory where the app's files are stored
                File externalFilesDir = getExternalFilesDir(null);
                if (externalFilesDir == null) {
                    throw new IOException("External storage not available");
                }

                // Create a copy of the CSV file with the desired name
                File csvFile = new File(externalFilesDir, "csvdata.csv");
                File bundleCsvFile = new File(externalFilesDir, "bundle.csv");
                copyFile(csvFile, bundleCsvFile);

                // Adjust IDs in bundle.csv to start from 0
                adjustIdsInBundleCsv(bundleCsvFile);

                // Add bundle.csv to the Bundle folder in the zip file
                addFileToZip(zos, bundleCsvFile, "Bundle/bundle.csv");

                // Add images from the images folder to the Bundle folder in the zip file
                File imagesFolder = new File(externalFilesDir, "images");
                if (imagesFolder.exists() && imagesFolder.isDirectory()) {
                    File[] imageFiles = imagesFolder.listFiles();
                    if (imageFiles != null) {
                        for (File imageFile : imageFiles) {
                            addFileToZip(zos, imageFile, "Bundle/" + imageFile.getName());
                        }
                    }
                }

                // Notify success
                System.out.println("Bundle exported successfully.");
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error exporting bundle: " + e.getMessage());
        }
    }

    private void adjustIdsInBundleCsv(File bundleCsvFile) {
        try {
            // Read the contents of the bundle CSV file
            List<String> lines = Files.readAllLines(bundleCsvFile.toPath(), StandardCharsets.UTF_8);

            // Modify the lines to adjust the IDs
            for (int i = 1; i < lines.size(); i++) { // Start from 1 to skip the header
                String[] parts = lines.get(i).split(",");
                int id = Integer.parseInt(parts[0]);
                parts[0] = String.valueOf(id - 1); // Adjust the ID
                lines.set(i, String.join(",", parts));
            }

            // Write the modified lines back to the bundle CSV file
            Files.write(bundleCsvFile.toPath(), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error adjusting IDs in bundle.csv: " + e.getMessage());
        }
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        FileInputStream fis = new FileInputStream(sourceFile);
        FileOutputStream fos = new FileOutputStream(destFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }
        fis.close();
        fos.close();
    }

    private void addFileToZip(ZipOutputStream zos, File file, String entryName) throws IOException {
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            fis.close();
        }
    }


    private void deleteImage(int position, Context context) {
        if (position >= 0 && position < dataCount) {
            // Get the image file to be deleted
            String imageName = imageNames[position];
            File imageFile = new File(getExternalFilesDir(null), "/images/" + imageName);

            // Check if the file exists and delete it
            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    Log.i("Main Activity", "Deleted image file: " + imageName);
                } else {
                    Log.e("Main Activity", "Failed to delete image file: " + imageName);
                    return; // Exit if deletion fails
                }
            } else {
                Log.e("Main Activity", "Image file not found: " + imageName);
                return; // Exit if file not found
            }

            // Remove the corresponding entry from the CSV file
            removeFromCSV(position);

            clearGlideCache(context);
            // Reload all data from the CSV file
            readCSV();

            // Reassign IDs sequentially
            for (int i = 0; i < dataCount; i++) {
                ids[i] = i; // Start from 1 and increment sequentially
            }

            // Update the CSV file with the re-assigned IDs
            updateIdsInCSV();
            // Clear and reload the adapter
            clearAndReloadAdapter();
            initializeMainActivity();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void clearGlideCache(final Context context) {
        Glide.get(context).clearMemory();
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                Glide.get(context).clearDiskCache();
                return null;
            }

            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                // Ensure that adapter.notifyDataSetChanged() is called on the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }.execute();
    }

    private void clearAndReloadAdapter() {
        // Clear the adapter completely

        // Clear the images and image labels lists
        images.clear();
        imageLables.clear();

        // Load labels from the updated CSV file
        loadLabelsFromCSV();

        // Load images from the updated CSV file
        loadImages(imageLables);

        // Notify the adapter that the data has changed
        adapter.updateData(images, imageLables);
        adapter.notifyDataSetChanged();
    }

    private void updateIdsInCSV() {
        StringBuilder csvData = new StringBuilder("id,label,count,imageName\n");

        // Start from the last index and decrement the ID value
        for (int i = dataCount - 1; i >= 0; i--) {
            csvData.append((dataCount - i - 1)).append(","); // Decrementing ID value
            csvData.append(labels[i]).append(",");
            csvData.append(counts[i]).append((","));
            csvData.append(imageNames[i]).append("\n");
        }

        // Now, write the updated CSV data back to the file
        try {
            File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");
            boolean isNewFile = false;

            if (!csvFile.exists()) {
                isNewFile = true;
                csvFile.createNewFile();
            }

            FileWriter writer = new FileWriter(csvFile, false);
            writer.write(csvData.toString());
            writer.flush();
            writer.close();

            if (isNewFile) {
                // Set appropriate permissions for the new file
                csvFile.setReadable(true);
                csvFile.setWritable(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromCSV(int position) {
        // Read the contents of the CSV file into a list
        List<String> csvLines = new ArrayList<>();
        try {
            File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            while ((line = reader.readLine()) != null) {
                csvLines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return; // Exit if reading CSV fails
        }

        // Remove the line corresponding to the deleted image
        if (position < csvLines.size()) {
            csvLines.remove(position + 1); // Adding 1 to account for the header line
        }

        // Write the updated CSV content back to the file
        try {
            File csvFile = new File(getExternalFilesDir(null), "csvdata.csv");
            FileWriter writer = new FileWriter(csvFile, false);
            for (String csvLine : csvLines) {
                writer.write(csvLine + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pickPdfFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    private String getPDFFileNameWithoutExtension(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();
        if (scheme != null && scheme.equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileName = cursor.getString(index);
                cursor.close();
            }
        }
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }
        // Remove the file extension, if present
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            fileName = fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    private void extractImagesFromPDF(Uri pdfUri) {
        try {
            // Get the PDF file name
            String pdfFileName = getPDFFileNameWithoutExtension(pdfUri);

            // Initialize PDF renderer
            PdfRenderer renderer = new PdfRenderer(getContentResolver().openFileDescriptor(pdfUri, "r"));

            // Create folder for saving images
            File folder = new File(getExternalFilesDir(null), "PDF_IMAGES");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Iterate through each page of the PDF
            for (int pageNumber = 0; pageNumber < renderer.getPageCount(); pageNumber++) {
                // Render the page
                PdfRenderer.Page page = renderer.openPage(pageNumber);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();

                // Save the bitmap as an image file
                String imageName = pdfFileName.substring(0) + "_page_" + (pageNumber + 1) + ".png";
                File imageFile = new File(folder, imageName);
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
                selectIndividualImages(imageName);

            }

            // Close the PDF renderer
            renderer.close();


        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception if any error occurs during PDF extraction
            Toast.makeText(this, "Failed to extract images from PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectIndividualImages(String imageName) {
        // Construct the source file path
        File sourceFile = new File(getExternalFilesDir(null), "PDF_IMAGES/" + imageName);

        // Construct the destination file path (where you want to save the cropped image)
        File destinationFile = new File(getExternalFilesDir(null), "cropped_image.jpg");

        try {
            // Start UCrop activity
            UCrop uCrop = UCrop.of(Uri.fromFile(sourceFile), Uri.fromFile(destinationFile)).withAspectRatio(1, 1);// Aspect ratio (optional)

            uCrop.start(MainActivity.this, UCrop.REQUEST_CROP); // Start UCrop activity with a request code

            //Toast.makeText(this, "Image successfully cropped", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeImagesFromRootFolder() {

        // Get the directory of the app's private folder
        File privateFolder = getExternalFilesDir(null);

        // Check if the private folder exists and is a directory
        if (privateFolder != null && privateFolder.isDirectory()) {
            // Get a list of all files in the directory
            File[] files = privateFolder.listFiles();

            // Iterate through each file in the directory
            if (files != null) {
                for (File file : files) {
                    // Check if the file is an image file (PNG, JPG, GIF, etc.)
                    if (isImageFile(file)) {
                        // Delete the image file
                        boolean deleted = file.delete();
                        if (deleted) {
                            Log.i("DeleteImageFiles", "Deleted file: " + file.getName());
                        } else {
                            Log.e("DeleteImageFiles", "Failed to delete file: " + file.getName());
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        sortCSVByCount(); // Call the method to sort CSV data by count when the app goes into the background
        sortImagesByCount();
        saveSettings();
        writePlanCSV();

    }

    public void onDestroy() {
        super.onDestroy();
        writePlanCSV();
        loadImagesFromPlanCSV();
    }

    public void onResume() {
        super.onResume();
        loadImagesFromPlanCSV();
    }

}