package xyz.lilyflower.solaris.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("unused")
public class SolarisMessageHelper {
    public static void message(EntityPlayer player, String key, boolean bold, boolean italic, boolean strikethrough, boolean underline, boolean obfuscated, EnumChatFormatting colour) {
        ChatComponentTranslation message = new ChatComponentTranslation(key);
        ChatStyle style = new ChatStyle();
        style.setBold(bold).setObfuscated(obfuscated).setItalic(italic).setStrikethrough(strikethrough).setUnderlined(underline).setColor(colour);
        message.setChatStyle(style);
        player.addChatMessage(message);
    }

    public static void message(EntityPlayer player, String key) {
        message(player, key, false, false, false, false, false, EnumChatFormatting.WHITE);
    }

    public static void messageColoured(EntityPlayer player, String key, EnumChatFormatting colour) {
        message(player, key, false, false, false, false, false, colour);
    }

    public static void messageBold(EntityPlayer player, String key) {
        message(player, key, true, false, false, false, false, EnumChatFormatting.WHITE);
    }

    public static void messageBoldColoured(EntityPlayer player, String key, EnumChatFormatting colour) {
        message(player, key, true, false, false, false, false, colour);
    }

    public static void messageItalic(EntityPlayer player, String key) {
        message(player, key, false, true, false, false, false, EnumChatFormatting.WHITE);
    }

    public static void messageItalicColoured(EntityPlayer player, String key, EnumChatFormatting colour) {
        message(player, key, false, true, false, false, false, colour);
    }

    public static void messageStrikethrough(EntityPlayer player, String key) {
        message(player, key, false, false, true, false, false, EnumChatFormatting.WHITE);
    }

    public static void messageStrikethroughColoured(EntityPlayer player, String key, EnumChatFormatting colour) {
        message(player, key, false, false, true, false, false, colour);
    }

    public static void messageUnderlined(EntityPlayer player, String key) {
        message(player, key, false, false, false, true, false, EnumChatFormatting.WHITE);
    }

    public static void messageUnderlinedColoured(EntityPlayer player, String key, EnumChatFormatting colour) {
        message(player, key, false, false, false, true, false, colour);
    }

    public static void messageObfuscated(EntityPlayer player, String key) {
        message(player, key, false, false, false, false, true, EnumChatFormatting.WHITE);
    }

    public static void messageObfuscatedColoured(EntityPlayer player, String key, EnumChatFormatting colour) {
        message(player, key, false, false, false, false, true, colour);
    }
}
