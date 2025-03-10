package fi.septicuss.tooltips.managers.integration.impl.laroc;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import it.coralrp.laroc.furniture.api.furnitures.Furniture;
import it.coralrp.laroc.furniture.api.furnitures.FurnitureContainer;
import it.coralrp.laroc.furniture.api.utils.FurnitureUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class LarocFurnitureProvider implements FurnitureProvider {

    private final FurnitureContainer furnitureContainer;

    public LarocFurnitureProvider() {
        this.furnitureContainer = Tooltips.get().getFurnitureModule().getFurnitureContainer();
    }

    @Override
    public FurnitureWrapper getFurniture(Block block) {
        if (block == null) return null;
        Optional<Furniture> furniture = furnitureContainer.getFurniture(block);
        return furniture.map(this::getWrapperFromFurniture).orElse(null);
    }

    @Override
    public FurnitureWrapper getFurniture(Entity entity) {
        if (entity == null) {
            return null;
        }

        if (!FurnitureUtils.isAFurniture(entity)) {
            return null;
        }

        Furniture furniture = furnitureContainer.getFurniture(entity).orElse(null);
        return this.getWrapperFromFurniture(furniture);
    }

    private FurnitureWrapper getWrapperFromFurniture(final Furniture furniture) {
        if (furniture == null) {
            return null;
        }

        final String id = furniture.getId();
        final String name = furniture.getStringName();

        return new FurnitureWrapper(this.identifier(), id, name);
    }

    @Override
    public String identifier() {
        return "Laroc";
    }
}
