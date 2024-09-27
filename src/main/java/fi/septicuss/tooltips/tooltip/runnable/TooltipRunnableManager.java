package fi.septicuss.tooltips.tooltip.runnable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.preset.PresetManager;
import fi.septicuss.tooltips.managers.preset.actions.ActionProperties.TooltipAction;
import fi.septicuss.tooltips.managers.condition.StatementHolder;
import fi.septicuss.tooltips.managers.title.TitleManager;
import fi.septicuss.tooltips.tooltip.TooltipManager;

public class TooltipRunnableManager {

	private TooltipManager tooltipManager;
	private TitleManager titleManager;

	private Map<String, Preset> presets = new LinkedHashMap<>();
	private Map<String, StatementHolder> holders = new LinkedHashMap<>();
	
	private TooltipRunnable runnable;

	// MANAGER
	
	public TooltipRunnableManager(Tooltips plugin) {
		this.tooltipManager = plugin.getTooltipManager();
		this.titleManager = plugin.getTitleManager();
		loadNecessaryData(plugin.getPresetManager());
	}
	
	private void loadNecessaryData(PresetManager presetManager) {
		for (var preset : presetManager.getConditionalPresets()) {
			String id = preset.getId();

			presets.put(id, preset);
			holders.put(id, preset.getStatementHolder());
		}
	}
	
	public void runActions(TooltipAction action, Player player) {
		if (runnable != null)
			runnable.runActions(action, player);
	}
	
	public void run(JavaPlugin plugin, int checkFrequency) {
		runnable = new TooltipRunnable(tooltipManager, titleManager, presets, holders);
		runnable.runTaskTimer(plugin, 0L, checkFrequency);
	}
	
	public void stop() {
		runnable.clearData();
		runnable.cancel();
		runnable = null;
	}
	
}
