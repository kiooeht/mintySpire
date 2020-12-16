package mintySpire;

import basemod.*;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PreStartGameSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import mintySpire.patches.metrics.MintyMetrics;
import mintySpire.ui.ModMinMaxSlider;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

@SpireInitializer
public class MintySpire implements
        PostInitializeSubscriber,
        EditStringsSubscriber,
        PreStartGameSubscriber,
        OnStartBattleSubscriber {

    private static SpireConfig modConfig = null;
    private static String modID;
    public static final Logger runLogger = LogManager.getLogger(MintySpire.class.getName());
    public static final boolean hasStSLib;
    public static boolean inkHeartCompatibility;
    public static Color removeColor = new Color(0xFF6563FF);
    public static Color addColor = new Color(0x7FFF00FF);

    private final ArrayList<ModColorDisplay> removeColorButtons = new ArrayList<>();
    private final ArrayList<ModColorDisplay> addColorButtons = new ArrayList<>();

    static {
        hasStSLib = Loader.isModLoaded("stslib");
        if (hasStSLib) {
            runLogger.info("Detected StSLib");
        }
    }

    public static void initialize() {
        BaseMod.subscribe(new MintySpire());
        setModID("mintySpire");

        try {
            Properties defaults = new Properties();
            defaults.put("ShowHalfHealth", Boolean.toString(true));
            defaults.put("ShowBossName", Boolean.toString(true));
            defaults.put("ShowMiniMap", Boolean.toString(false));
            defaults.put("MiniMapIconScale", Float.toString(2.5f));
            defaults.put("Ironchad", Boolean.toString(true));
            defaults.put("SummedDamage", Boolean.toString(true));
            defaults.put("TotalIncomingDamage", Boolean.toString(true));
            defaults.put("RemoveBaseKeywords", Boolean.toString(false));
            defaults.put("ShowEchoFormReminder", Boolean.toString(true));
            defaults.put("WarnItemAffordability", Boolean.toString(true));
            defaults.put("MakeHandTransparent", Boolean.toString(true));
            defaults.put("HandOpacity", Float.toString(0.5f));
            defaults.put("ShowUpdatePreview", Boolean.toString(true));
            defaults.put("UpdatePreviewAddColor", addColor.toString());
            defaults.put("UpdatePreviewRemoveColor", removeColor.toString());
            modConfig = new SpireConfig("MintySpire", "Config", defaults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean showHH() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("ShowHalfHealth");
    }

    public static boolean showBN() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("ShowBossName");
    }

    public static boolean showIC() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("Ironchad");
    }

    public static boolean showSD() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("SummedDamage");
    }

    public static boolean showTID() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("TotalIncomingDamage");
    }

    public static boolean showBKR() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("RemoveBaseKeywords");
    }

    public static boolean showIU() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("WarnItemAffordability");
    }

    public static boolean makeHandTransparent() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("MakeHandTransparent");
    }

    public static float getHandOpacity() {
        if (modConfig == null) {
            return 1f;
        }
        return modConfig.getFloat("HandOpacity");
    }

    public static boolean showMM() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("ShowMiniMap");
    }

    public static float scaleMMIcons() {
        if (modConfig == null) {
            return 2.5f;
        }
        return modConfig.getFloat("MiniMapIconScale");
    }

    public static boolean showEFR() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("ShowEchoFormReminder");
    }

    public static boolean showBCUP() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("ShowUpdatePreview");
    }

    /*public static Color getBCUPAddColor() {
        if (modConfig == null) {
            return addColor;
        }

        String colString = modConfig.getString("UpdatePreviewAddColor");
        return Color.valueOf(colString);
    }

    public static Color getBCUPRemoveColor() {
        if (modConfig == null) {
            return removeColor;
        }

        String colString = modConfig.getString("UpdatePreviewRemoveColor");
        return Color.valueOf(colString);
    }*/

    @Override
    public void receivePostInitialize() {
        runLogger.info("Minty Spire is active.");

        UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(MintySpire.makeID("OptionsMenu"));
        String[] TEXT = UIStrings.TEXT;
        addColor = Color.valueOf(modConfig.getString("UpdatePreviewAddColor"));
        removeColor = Color.valueOf(modConfig.getString("UpdatePreviewRemoveColor"));

        int xPos = 350, yPos = 750;
        ModPanel settingsPanel = new ModPanel();
        ModLabeledToggleButton HHBtn = new ModLabeledToggleButton(TEXT[0], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showHH(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("ShowHalfHealth", button.enabled);
                        saveConfig();
                    }
                });
        settingsPanel.addUIElement(HHBtn);
        yPos -= 50;

        ModLabeledToggleButton BNBtn = new ModLabeledToggleButton(TEXT[1], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showBN(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("ShowBossName", button.enabled);
                        saveConfig();
                    }
                });
        settingsPanel.addUIElement(BNBtn);
        yPos -= 50;

        ModLabeledToggleButton MMBtn = new ModLabeledToggleButton(TEXT[6], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showMM(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("ShowMiniMap", button.enabled);
                        saveConfig();
                    }
                });
        settingsPanel.addUIElement(MMBtn);
        yPos -= 50;

        ModLabel MMIconScaleLabel = new ModLabel(TEXT[7], xPos + 40, yPos + 8, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, l -> {
        });
        settingsPanel.addUIElement(MMIconScaleLabel);
        float textWidth = FontHelper.getWidth(FontHelper.charDescFont, TEXT[7], 1f / Settings.scale);

        ModMinMaxSlider MMIconScaleSlider = new ModMinMaxSlider("", xPos + 100 + textWidth, yPos + 15, 1, 4, scaleMMIcons(), "x%.2f", settingsPanel, slider -> {
            if (modConfig != null) {
                modConfig.setFloat("MiniMapIconScale", slider.getValue());
                saveConfig();
            }
        });
        settingsPanel.addUIElement(MMIconScaleSlider);
        yPos -= 50;

        if (Settings.language == Settings.GameLanguage.ENG || Settings.language == Settings.GameLanguage.ZHS) {
            ModLabeledToggleButton ICBtn = new ModLabeledToggleButton(TEXT[2], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showIC(), settingsPanel, l -> {
            },
                    button ->
                    {
                        if (modConfig != null) {
                            modConfig.setBool("Ironchad", button.enabled);
                            saveConfig();
                        }
                    });
            settingsPanel.addUIElement(ICBtn);
            yPos -= 50;
        }

        ModLabeledToggleButton EFBtn = new ModLabeledToggleButton(TEXT[8], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showSD(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("ShowEchoFormReminder", button.enabled);
                        saveConfig();
                    }
                });
        settingsPanel.addUIElement(EFBtn);
        yPos -= 50;

        ModLabeledToggleButton SBBtn = new ModLabeledToggleButton(TEXT[3], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showSD(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("SummedDamage", button.enabled);
                        saveConfig();
                    }
                });
        settingsPanel.addUIElement(SBBtn);
        yPos -= 50;

        ModLabeledToggleButton TIDBtn = new ModLabeledToggleButton(TEXT[4], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showTID(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("TotalIncomingDamage", button.enabled);
                        saveConfig();
                    }
                });
        settingsPanel.addUIElement(TIDBtn);
        yPos -= 50;

        ModLabeledToggleButton BKRBtn = new ModLabeledToggleButton(TEXT[5], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showBKR(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("RemoveBaseKeywords", button.enabled);
                        saveConfig();
                    }
                });
        settingsPanel.addUIElement(BKRBtn);
        yPos -= 50;

        ModLabeledToggleButton WIUBtn = new ModLabeledToggleButton(TEXT[9], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showIU(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("WarnItemAffordability", button.enabled);
                        saveConfig();
                    }
                });
        settingsPanel.addUIElement(WIUBtn);
        yPos -= 50;

        ModLabeledToggleButton HTBtn = new ModLabeledToggleButton(TEXT[10], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, makeHandTransparent(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("MakeHandTransparent", button.enabled);
                        saveConfig();
                    }
                });
        settingsPanel.addUIElement(HTBtn);
        textWidth = FontHelper.getWidth(FontHelper.charDescFont, TEXT[10], 1f / Settings.scale);

        ModMinMaxSlider HandOpacitySlider = new ModMinMaxSlider("", xPos + (100f * Settings.scale) + textWidth, yPos + (15f * Settings.scale), 0, 1, getHandOpacity(), "%.2f", settingsPanel, slider -> {
            if (modConfig != null) {
                modConfig.setFloat("HandOpacity", slider.getValue());
                saveConfig();
            }
        });
        settingsPanel.addUIElement(HandOpacitySlider);
        yPos -= 50f;

        //Better Card Upgrade Preview
        ModLabeledToggleButton BCUPBtn = new ModLabeledToggleButton(TEXT[11], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, showBCUP(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("ShowUpdatePreview", button.enabled);
                        saveConfig();
                    }
                });
        settingsPanel.addUIElement(BCUPBtn);
        yPos -= 50;

        Texture colorButton = new Texture(getModID() + "Resources/img/colorButton.png");
        Texture colorButtonOutline = new Texture(getModID() + "Resources/img/colorButtonOutline.png");
        Consumer<ModColorDisplay> handleRemoveClick = modColorDisplay -> {
            removeColorButtons.forEach(m -> {
                m.rOutline = Color.DARK_GRAY.r;
                m.gOutline = Color.DARK_GRAY.g;
                m.bOutline = Color.DARK_GRAY.b;
            });
            modColorDisplay.rOutline = Color.GOLDENROD.r;
            modColorDisplay.gOutline = Color.GOLDENROD.g;
            modColorDisplay.bOutline = Color.GOLDENROD.b;
            removeColor = new Color(modColorDisplay.r, modColorDisplay.g, modColorDisplay.b, 1.0f);
            modConfig.setString("UpdatePreviewRemoveColor", removeColor.toString());
            saveConfig();
        };
        Consumer<ModColorDisplay> handleAddClick = modColorDisplay -> {
            addColorButtons.forEach(m -> {
                m.rOutline = Color.DARK_GRAY.r;
                m.gOutline = Color.DARK_GRAY.g;
                m.bOutline = Color.DARK_GRAY.b;
            });
            modColorDisplay.rOutline = Color.GOLDENROD.r;
            modColorDisplay.gOutline = Color.GOLDENROD.g;
            modColorDisplay.bOutline = Color.GOLDENROD.b;
            addColor = new Color(modColorDisplay.r, modColorDisplay.g, modColorDisplay.b, 1.0f);
            modConfig.setString("UpdatePreviewAddColor", addColor.toString());
            saveConfig();
        };

        float descWidth = NumberUtils.max(FontHelper.getSmartWidth(FontHelper.charDescFont, TEXT[12], 9999.0F, 0.0F), FontHelper.getSmartWidth(FontHelper.charDescFont, TEXT[13], 9999.0F, 0.0F));
        settingsPanel.addUIElement(new ModLabel(TEXT[12], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, (modLabel -> { })));

        List<Color> addColors = new ArrayList<>();
        addColors.add(new Color(0x7FFF00FF));
        addColors.add(new Color(0xE1FF00FF));
        addColors.add(new Color(0x00FFF7FF));
        addColors.add(new Color(0x0095FFFF));
        addColors.add(new Color(0xc300FFFF));
        for (int i = 0; i < addColors.size(); i++) {
            ModColorDisplay modColorDisplay = new ModColorDisplay(xPos + descWidth + i * 96f, yPos - (10f * Settings.scale), 0f, colorButton, colorButtonOutline, handleAddClick);
            Color color = addColors.get(i);
            modColorDisplay.r = color.r;
            modColorDisplay.g = color.g;
            modColorDisplay.b = color.b;
            if (color.equals(addColor)) {
                modColorDisplay.rOutline = Color.GOLDENROD.r;
                modColorDisplay.gOutline = Color.GOLDENROD.g;
                modColorDisplay.bOutline = Color.GOLDENROD.b;
            } else {
                modColorDisplay.rOutline = Color.DARK_GRAY.r;
                modColorDisplay.gOutline = Color.DARK_GRAY.g;
                modColorDisplay.bOutline = Color.DARK_GRAY.b;
            }
            addColorButtons.add(modColorDisplay);
            settingsPanel.addUIElement(modColorDisplay);
        }
        yPos -= 50f;

        settingsPanel.addUIElement(new ModLabel(TEXT[13], xPos, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, (modLabel -> { })));

        List<Color> removeColors = new ArrayList<>();
        removeColors.add(new Color(0xFF6563FF));
        removeColors.add(new Color(0x666666FF));
        removeColors.add(new Color(0x5c1500FF));
        removeColors.add(new Color(0x5c3500FF));
        removeColors.add(new Color(0x003673FF));
        for (int i = 0; i < removeColors.size(); i++) {
            ModColorDisplay modColorDisplay = new ModColorDisplay(xPos + descWidth + i * 96f, yPos - (10f * Settings.scale), 0f, colorButton, colorButtonOutline, handleRemoveClick);
            Color color = removeColors.get(i);
            modColorDisplay.r = color.r;
            modColorDisplay.g = color.g;
            modColorDisplay.b = color.b;
            if (color.equals(removeColor)) {
                modColorDisplay.rOutline = Color.GOLDENROD.r;
                modColorDisplay.gOutline = Color.GOLDENROD.g;
                modColorDisplay.bOutline = Color.GOLDENROD.b;
            } else {
                modColorDisplay.rOutline = Color.DARK_GRAY.r;
                modColorDisplay.gOutline = Color.DARK_GRAY.g;
                modColorDisplay.bOutline = Color.DARK_GRAY.b;
            }
            removeColorButtons.add(modColorDisplay);
            settingsPanel.addUIElement(modColorDisplay);
        }
        yPos -= 50f;

        BaseMod.registerModBadge(ImageMaster.loadImage(getModID() + "Resources/img/modBadge.png"), getModID(), "erasels, kiooeht", "TODO", settingsPanel);

        BaseMod.addSaveField("MintyMetricActs", new CustomSavable<ArrayList<String>>() {
            @Override
            public ArrayList<String> onSave() {
                return MintyMetrics.acts;
            }

            @Override
            public void onLoad(ArrayList<String> i) {
                if (i == null) {
                    MintyMetrics.acts = new ArrayList<>();
                } else {
                    MintyMetrics.acts = i;
                }
            }
        });
    }


    @Override
    public void receiveEditStrings() {
        loadLocStrings("eng");
        if (!languageSupport().equals("eng")) {
            loadLocStrings(languageSupport());
        }
    }

    public static String getModID() {
        return modID;
    }

    public static void setModID(String id) {
        modID = id;
    }

    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }

    private String languageSupport() {
        switch (Settings.language) {
            case ZHS:
                return "zhs";
            case KOR:
                return "kor";
            default:
                return "eng";
        }
    }

    private void loadLocStrings(String language) {
        BaseMod.loadCustomStringsFile(UIStrings.class, getModID() + "Resources/localization/" + language + "/UI-Strings.json");
    }

    @Override
    public void receivePreStartGame() {
        inkHeartCompatibility = false;
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        inkHeartCompatibility = AbstractDungeon.player.hasRelic("wanderingMiniBosses:Inkheart");
    }

    private void saveConfig() {
        try {
            modConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
