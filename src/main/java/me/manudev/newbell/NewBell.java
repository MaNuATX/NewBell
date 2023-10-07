package me.manudev.newbell;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NewBell extends JavaPlugin implements Listener {

    private final Map<Inventory, String> guiHolder = new HashMap<>();
    private final Map<Inventory, String> secondGuiHolder = new HashMap<>();
    private final Map<Inventory, String> ambulanceGuiHolder = new HashMap<>();
    private final String guiName = "§cEmergenze";
    private final String secondGuiName = "§cPompieri";
    private final String ambulanceGuiName = "§cAmbulanza";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        if (action == Action.RIGHT_CLICK_BLOCK && block != null) {
            if (block.getType() == Material.BELL) {
                event.setCancelled(true);
                openBellGUI(player);
            }
        }
    }

    private void openBellGUI(Player player) {
        Inventory gui = getServer().createInventory(null, 27, guiName);
        guiHolder.put(gui, guiName);

        ItemStack glassPane = createItem(Material.GRAY_STAINED_GLASS_PANE, "§cEmergenze");

        for (int i = 0; i < 27; i++) {
            if (i != 10 && i != 13 && i != 16) {
                gui.setItem(i, glassPane);
            }
        }

        ItemStack pompieri = createItem(Material.REDSTONE, "§cPompieri");
        ItemStack ambulanza = createItem(Material.BONE_MEAL, "§fAmbulanza");
        ItemStack polizia = createItem(Material.DIAMOND_SWORD, "§9Polizia");

        ItemMeta pompieriMeta = pompieri.getItemMeta();
        ItemMeta ambulanzaMeta = ambulanza.getItemMeta();
        ItemMeta poliziaMeta = polizia.getItemMeta();

        pompieriMeta.setLore(Arrays.asList("§8» §7Clicca qui per richiedere un intervento dei §cPompieri§7."));
        ambulanzaMeta.setLore(Arrays.asList("§8» §7Clicca qui per richiedere un intervento dell'§fAmbulanza§7."));
        poliziaMeta.setLore(Arrays.asList("§8» §7Clicca qui per richiedere un intervento della §9Polizia§7."));

        pompieri.setItemMeta(pompieriMeta);
        ambulanza.setItemMeta(ambulanzaMeta);
        polizia.setItemMeta(poliziaMeta);

        gui.setItem(10, pompieri);
        gui.setItem(13, ambulanza);
        gui.setItem(16, polizia);

        player.openInventory(gui);
    }

    private void openAmbulanceGUI(Player player) {
        Inventory gui = getServer().createInventory(null, 27, ambulanceGuiName);
        ambulanceGuiHolder.put(gui, ambulanceGuiName);

        ItemStack glassPane = createItem(Material.GRAY_STAINED_GLASS_PANE, "§cAmbulanza");

        ItemStack brokenLeg = createItem(Material.POPPY, "§cGamba rotta");
        ItemStack sickness = createItem(Material.POPPY, "§cMalessere");

        for (int i = 0; i < 27; i++) {
            if (i == 12) {
                gui.setItem(i, brokenLeg);
            } else if (i == 14) {
                gui.setItem(i, sickness);
            } else {
                gui.setItem(i, glassPane);
            }
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onAmbulanceInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        String inventoryName = ambulanceGuiHolder.get(clickedInventory);

        if (inventoryName != null && inventoryName.equals(ambulanceGuiName)) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.POPPY) {
                String callType = clickedItem.getItemMeta().getDisplayName();
                String message;

                if (callType.equals("§cGamba rotta")) {
                    message = "§8-------------------§7[§c§lEMERGENZE§7]§8-------------------\n" +
                            "   §7Il Cittadino §f" + player.getName() + " §7Ha richiesto l'intervento di un §fAmbulanza.\n" +
                            "                          §7per una §cGamba rotta\n" +
                            "                       §7Si trova a §f" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + "\n" +
                            "§8-------------------§7[§c§lEMERGENZE§7]§8-------------------";
                } else if (callType.equals("§cMalessere")) {
                    message = "§8-------------------§7[§c§lEMERGENZE§7]§8-------------------\n" +
                            "   §7Il Cittadino §f" + player.getName() + " §7Ha richiesto l'intervento di un §fAmbulanza.\n" +
                            "                          §7per un §cMalessere\n" +
                            "                       §7Si trova a §f" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + "\n" +
                            "§8-------------------§7[§c§lEMERGENZE§7]§8-------------------";
                } else {
                    return; // No need to continue if it's not a recognized call type
                }

                player.sendMessage("§8» §7Hai chiamato l'§cAmbulanza§7. Rimani sulla tua posizione attuale per avvantaggiare l'§cAmbulanza§7.");

                for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("newbell.ambulanza")) {
                        onlinePlayer.sendMessage(message);
                    }
                }

                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BELL_USE, 1.0f, 1.0f); // Riproduci il suono
                player.closeInventory(); // Chiudi l'inventario dopo la selezione
                event.setCancelled(true);
            }
        }
    }


    private void openSecondGUI(Player player) {
        Inventory gui = getServer().createInventory(null, 27, secondGuiName);
        secondGuiHolder.put(gui, secondGuiName);

        ItemStack glassPane = createItem(Material.GRAY_STAINED_GLASS_PANE, "§cPompieri");

        ItemStack lavaIncendio = createItem(Material.LAVA_BUCKET, "§4Incendio");
        ItemStack lavaStuck = createItem(Material.LAVA_BUCKET, "§4Stuck");

        for (int i = 0; i < 27; i++) {
            if (i == 12) {
                gui.setItem(i, lavaIncendio);
            } else if (i == 14) {
                gui.setItem(i, lavaStuck);
            } else {
                gui.setItem(i, glassPane);
            }
        }

        player.openInventory(gui);
    }

    private ItemStack createItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        String inventoryName = guiHolder.get(clickedInventory);

        if (inventoryName != null && inventoryName.equals(guiName)) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null) {
                if (clickedItem.getType() == Material.REDSTONE) {
                    // Attiva la seconda GUI
                    openSecondGUI(player);
                } else if (clickedItem.getType() == Material.BONE_MEAL || clickedItem.getType() == Material.DIAMOND_SWORD) {
                    openAmbulanceGUI(player);
                } else if (clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                    player.closeInventory();
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSecondInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        String inventoryName = secondGuiHolder.get(clickedInventory);

        if (inventoryName != null && inventoryName.equals(secondGuiName)) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.LAVA_BUCKET) {
                String lavaType = clickedItem.getItemMeta().getDisplayName();
                String message;

                if (lavaType.equals("§4Incendio")) {
                    message = "§8-------------------§7[§c§lEMERGENZE§7]§8-------------------\n" +
                            "   §7Il Cittadino §f" + player.getName() + " §7Ha richiesto l'intervento dei §cPompieri.\n" +
                            "                          §7per un §4Incendio§7.\n" +
                            "                       §7Si trova a §f" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + "\n" +
                            "§8-------------------§7[§c§lEMERGENZE§7]§8-------------------";
                } else if (lavaType.equals("§4Stuck")) {
                    message = "§8-------------------§7[§c§lEMERGENZE§7]§8-------------------\n" +
                            "   §7Il Cittadino §f" + player.getName() + " §7Ha richiesto l'intervento dei §cPompieri.\n" +
                            "                          §7perchè è §4Bloccato§7.\n" +
                            "                       §7Si trova a §f" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + "\n" +
                            "§8-------------------§7[§c§lEMERGENZE§7]§8-------------------";
                } else {
                    return; // No need to continue if it's not a recognized lava type
                }

                player.sendMessage("§8» §7Hai chiamato i §cPompieri§7. Rimani sulla tua posizione attuale per avvantaggiare i §cPompieri§7.");

                for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("newbell.pompieri")) {
                        onlinePlayer.sendMessage(message);
                    }
                }

                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BELL_USE, 1.0f, 1.0f); // Play sound here
                player.closeInventory(); // Chiude l'inventario dopo la selezione
                event.setCancelled(true);
            }
        }
    }
}

