package com.cburch.logisim.std.ttl;

import java.awt.Font;
import java.awt.Graphics;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.std.memory.ShiftRegisterData;
import com.cburch.logisim.util.GraphicsUtil;

public class Ttl74165 extends AbstractTtlGate {

	public Ttl74165() {
		super("74165", (byte) 16, new byte[] { 7, 9 }, new String[] { "Shift/Load", "Clock", "P4", "P5", "P6", "P7",
				"Q7n", "Q7", "Serial Input", "P0", "P1", "P2", "P3", "Clock Inhibit" });
	}

	private ShiftRegisterData getData(InstanceState state) {
		ShiftRegisterData data = (ShiftRegisterData) state.getData();
		if (data == null) {
			data = new ShiftRegisterData(BitWidth.ONE, 8);
			state.setData(data);
		}
		return data;
	}

	@Override
	public void paintInternal(InstancePainter painter, int x, int y, int height, boolean up) {
		Graphics g = painter.getGraphics();
		super.paintBase(painter, false, false);
		Drawgates.paintPortNames(painter, x, y, height, new String[] { "ShLd", "CK", "P4", "P5", "P6", "P7", "Q7n",
				"Q7", "SER", "P0", "P1", "P2", "P3", "CkIh" });
		ShiftRegisterData data = getData(painter);
		String s = "";
		for (byte i = 0; i < 8; i++)
			s += data.get(7 - i).toHexString();
		g.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 14));
		GraphicsUtil.drawCenteredText(g, s, x + 80, y + height / 2 - 3);
	}

	@Override
	public void ttlpropagate(InstanceState state) {
		ShiftRegisterData data = getData(state);
		boolean triggered = data.updateClock(state.getPort(1), StdAttr.TRIG_RISING);
		if (triggered && state.getPort(13) != Value.TRUE) {
			if (state.getPort(0) == Value.FALSE) {// load
				data.clear();
				data.push(state.getPort(9));
				data.push(state.getPort(10));
				data.push(state.getPort(11));
				data.push(state.getPort(12));
				data.push(state.getPort(2));
				data.push(state.getPort(3));
				data.push(state.getPort(4));
				data.push(state.getPort(5));
			} else if (state.getPort(0) == Value.TRUE) {// shift
				data.push(state.getPort(8));
			}
		}
		state.setPort(6, data.get(0).not(), 4);
		state.setPort(7, data.get(0), 4);
	}
}
