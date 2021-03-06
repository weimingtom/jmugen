package org.lee.mugen.background.bgCtrlFunction;

import java.util.ArrayList;

import org.lee.mugen.background.BG;
import org.lee.mugen.core.GameFight;
import org.lee.mugen.core.gameSelect.GameSelect;
import org.lee.mugen.parser.type.Valueable;
import org.lee.mugen.sprite.cns.eval.function.StateCtrlFunction;
import org.lee.mugen.sprite.parser.Parser;
import org.lee.mugen.stage.Stage;

/**
 * 
 * @author Dr Wong
 * Test if the player is alive
 * @category Trigger : Complete
 */
public class Siny extends StateCtrlFunction {

	public Siny() {
		super("siny", new String[] {"value"});
	}
	@Override
	public Object getValue(String bgId, Valueable... params) {
		Stage stage = GameFight.getInstance().getStage();
		if (stage == null) {
			// call from preview in gameSelect
			stage = GameSelect.getInstance().getStage();
		}
		ArrayList<BG> bgs = stage.getBackground().getBgCtrlDefMap().get(new Integer(bgId)).getBgCopys();

		for (BG bg: bgs) {

			int xIndex = getParamIndex("value");
			if (valueableParams[xIndex] != null) {
				Valueable x = valueableParams[xIndex][0];
				if (x != null) {
					int ix = Parser.getIntValue(x.getValue(bgId));
					bg.setSinYAmp(ix);
				}
				if (valueableParams[xIndex].length > 1) {
					Valueable y = valueableParams[xIndex][1];
					if (y != null) {
						int iy = Parser.getIntValue(y.getValue(bgId));
						bg.setSinYTime(iy);
					}		
				}
			}
			
		}

		return null;
	}
}
