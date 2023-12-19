package net.boster.escape.from.tarkov.utils.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BosterSound {

    public Object sound;
    public int i = 1;
    public int loudness = 1;

    public static BosterSound load(String s) {
        if(s == null) {
            return null;
        }

        BosterSound b = new BosterSound();
        if(!s.contains(":")) {
            try {
                b.sound = Sound.valueOf(s.toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }

        String[] ss = s.split(":");
        if(ss.length == 2) {
            try {
                b.sound = Sound.valueOf(ss[0]);
                b.i = Integer.parseInt(ss[1]);
            } catch (Exception e) {
                b.sound = ss[0] + ":" + ss[1];
            }
        } else if(ss.length == 3) {
            try {
                b.sound = Sound.valueOf(ss[0]);
                b.i = Integer.parseInt(ss[1]);
                b.loudness = Integer.parseInt(ss[2]);
            } catch (Exception e) {
                try {
                    b.sound = ss[0] + ":" + ss[1];
                    b.i = Integer.parseInt(ss[2]);
                } catch (Exception e1) {
                    return null;
                }
            }
        } else if(ss.length == 4) {
            try {
                b.sound = Sound.valueOf(ss[0]);
                b.i = Integer.parseInt(ss[1]);
                b.loudness = Integer.parseInt(ss[2]);
            } catch (Exception e) {
                try {
                    b.sound = ss[0] + ":" + ss[1];
                    b.i = Integer.parseInt(ss[2]);
                    b.loudness = Integer.parseInt(ss[3]);
                } catch (Exception e1) {
                    return null;
                }
            }
        }

        return b;
    }

    public void play(Player p) {
        if(sound instanceof String) {
            p.playSound(p.getLocation(), (String) sound, loudness, i);
        } else if(sound instanceof Sound) {
            p.playSound(p.getLocation(), (Sound) sound, loudness, i);
        }
    }

    public void play(Location loc) {
        if(sound instanceof String) {
            loc.getWorld().playSound(loc, (String) sound, loudness, i);
        } else if(sound instanceof Sound) {
            loc.getWorld().playSound(loc, (Sound) sound, loudness, i);
        }
    }

    public void play(Player p, int loudness) {
        if(sound instanceof String) {
            p.playSound(p.getLocation(), (String) sound, loudness, i);
        } else if(sound instanceof Sound) {
            p.playSound(p.getLocation(), (Sound) sound, loudness, i);
        }
    }

    public void play(Location loc, int loudness) {
        if(sound instanceof String) {
            loc.getWorld().playSound(loc, (String) sound, loudness, i);
        } else if(sound instanceof Sound) {
            loc.getWorld().playSound(loc, (Sound) sound, loudness, i);
        }
    }
}
