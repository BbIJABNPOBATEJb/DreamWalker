package me.bbijabnpobatejb.dreamwalker.scheduler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    static final List<Task> serverTasks = new ArrayList<>();

    @SubscribeEvent
    public void on(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (Task task : serverTasks) {
            if (task.getTickDealy() > 0) continue;
            task.getRunnable().run();
        }
        serverTasks.removeIf(t -> t.getTickDealy() <= 0);

        for (Task task : serverTasks) {
            task.setTickDealy(task.getTickDealy() - 1);
        }
    }

    public static void runTask(Runnable runnable, long tickDealy) {
        serverTasks.add(new Task(runnable, tickDealy));
    }

    public static void reset() {
        serverTasks.removeIf(t -> true);
    }
}
