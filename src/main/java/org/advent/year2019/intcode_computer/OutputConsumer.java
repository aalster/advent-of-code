package org.advent.year2019.intcode_computer;

import java.util.List;

public interface OutputConsumer {
	
	void accept(long output);
	
	static OutputConsumer empty() {
		return o -> {};
	}
	
	static OutputConsumer printer() {
		return o -> {
			if (o < 256)
				System.out.print((char) o);
			else
				System.out.println(o);
		};
	}
	
	static BufferingTextOutputConsumer bufferingText() {
		return new BufferingTextOutputConsumer();
	}
	
	static OutputConsumer combine(OutputConsumer... consumers) {
		if (consumers == null || consumers.length == 0)
			return empty();
		if (consumers.length == 1)
			return consumers[0];
		return o -> List.of(consumers).forEach(c -> c.accept(o));
	}
	
	class BufferingTextOutputConsumer implements OutputConsumer {
		private final StringBuilder buffer = new StringBuilder();
		
		@Override
		public void accept(long output) {
			buffer.append((char) output);
		}
		
		public String read() {
			String result = buffer.toString();
			buffer.setLength(0);
			return result;
		}
	}
}
