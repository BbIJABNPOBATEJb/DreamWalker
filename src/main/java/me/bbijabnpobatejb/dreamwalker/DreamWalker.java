package me.bbijabnpobatejb.dreamwalker;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import lombok.Getter;
import me.bbijabnpobatejb.dreamwalker.alias.AliasCommand;
import me.bbijabnpobatejb.dreamwalker.alias.RunAliasHandler;
import me.bbijabnpobatejb.dreamwalker.command.DreamWalkerCommand;
import me.bbijabnpobatejb.dreamwalker.config.JsonHandler;
import me.bbijabnpobatejb.dreamwalker.effects.EffectsCommand;
import me.bbijabnpobatejb.dreamwalker.scheduler.Scheduler;
import me.bbijabnpobatejb.dreamwalker.side.CommonProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Getter
@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, name = Reference.NAME)
public class DreamWalker {
    @SidedProxy(
            clientSide = "me.bbijabnpobatejb.dreamwalker.side.ClientProxy",
            serverSide = "me.bbijabnpobatejb.dreamwalker.side.CommonProxy"
    )
    public static CommonProxy proxy;
    @Getter
    private static final Logger logger = LogManager.getLogger(Reference.MOD_ID);
    @Getter
    private static DreamWalker instance;
    File modConfigurationDirectory;

    public static final int MAX_CHAT_CHAR = 512;

    public static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper(Reference.MOD_ID);

    JsonHandler config;
//    @Setter
//    SQLiteManager database;

    public DreamWalker() {
        instance = this;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        modConfigurationDirectory = event.getModConfigurationDirectory();
        proxy.preInit(event);

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        config = new JsonHandler(modConfigurationDirectory, Reference.MOD_ID);
        event.registerServerCommand(new EffectsCommand());
        event.registerServerCommand(new DreamWalkerCommand());
        event.registerServerCommand(new AliasCommand());

//        File dbFile = new File(modConfigurationDirectory, "dreamwalker.db");

//        database = new SQLiteManager();
//        database.connect(dbFile.getAbsolutePath());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStoppingEvent event) {
        Scheduler.reset();
//        database.shutdown();
    }
}
