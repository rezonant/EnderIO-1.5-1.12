package crazypants.enderio.waila;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.IConduitBundle.FacadeRenderState;
import crazypants.util.Lang;
import crazypants.util.StringUtil;
import crazypants.util.WailaUtil;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class WailaConduitBundle extends WailaDataProvider {
	
	public String getModuleName() {
		return "Conduits";
	}
	
	@Override
	public String getDisplayName(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		
		Block block = EnderIO.blockConduitBundle;
		TileEntity te = accessor.getTileEntity();
		String line = block.getLocalizedName();
		
		if (te instanceof TileConduitBundle) {
			TileConduitBundle bundle = (TileConduitBundle)te;
			int facadeId = bundle.getFacadeId();
			
			if (bundle.getFacadeRenderedAs() == FacadeRenderState.FULL) {
				if (facadeId > 0) {
					Block facadeBlock = Block.blocksList[facadeId];
					line = facadeBlock.getLocalizedName()+" "+WailaUtil.DARK_GRAY+" ("+Lang.localize("facade")+")";
				} else {
					line = block.getLocalizedName();
				}
			} else {
				Collection<IConduit> conduits = bundle.getConduits();
				
				if (conduits.size() == 1) {
					for (IConduit conduit : conduits) {
						line = conduit.createItem().getDisplayName();
						break;
					}
				} else {
					line = block.getLocalizedName();
				}
			}
		}

		return line;
	}
	
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {

		Block block = EnderIO.blockConduitBundle;
		TileEntity te = accessor.getTileEntity();
		
		if (te instanceof TileConduitBundle) {
			TileConduitBundle bundle = (TileConduitBundle)te;
			Collection<IConduit> conduits = bundle.getConduits();
			
			if (conduits.size() > 1) {
				List<String> types = new ArrayList<String>();
				
				for (IConduit conduit : conduits) {
					types.add(Lang.localizeShortName(conduit.createItem()));
				}
				
				currenttip.add(WailaUtil.DARK_GRAY+StringUtil.join(types, " • "));
			}
		}
		
		return currenttip;
	}
}
