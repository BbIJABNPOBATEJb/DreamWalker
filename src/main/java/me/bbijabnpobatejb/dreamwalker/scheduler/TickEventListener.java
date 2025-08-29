package me.bbijabnpobatejb.dreamwalker.scheduler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class TickEventListener {

    static final List<Task> tasks = new ArrayList<>();

    @SubscribeEvent
    public void on(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (Task task : tasks) {
            if (task.getTickDealy() > 0) continue;
            task.getRunnable().run();
        }
        tasks.removeIf(t -> t.getTickDealy() <= 0);

        for (Task task : tasks) {
            task.setTickDealy(task.getTickDealy() - 1);
        }
    }

    public static void runTask(Runnable runnable, long tickDealy) {
        tasks.add(new Task(runnable, tickDealy));
    }
}
