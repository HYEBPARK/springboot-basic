package org.programmers.kdt.weekly.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.programmers.kdt.weekly.command.io.Console;
import org.programmers.kdt.weekly.command.io.InfoMessageType;
import org.programmers.kdt.weekly.voucher.controller.dto.VoucherDto;
import org.programmers.kdt.weekly.voucher.model.VoucherType;
import org.programmers.kdt.weekly.voucher.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VoucherCommandLine {

	private final Logger log = LoggerFactory.getLogger(VoucherCommandLine.class);

	private final Console console;
	private final VoucherService voucherService;

	public VoucherCommandLine(Console console,
		VoucherService voucherService) {
		this.console = console;
		this.voucherService = voucherService;
	}

	public void run() {
		VoucherCommandType voucherCommandType = VoucherCommandType.DEFAULT;

		while (voucherCommandType.isRunnable()) {
			this.console.printCommandDescription(getCommandDescription());
			var userInput = this.console.getUserInput();

			try {
				voucherCommandType = VoucherCommandType.of(userInput);

				switch (voucherCommandType) {
					case VOUCHER_CREATE -> this.createVoucher();
					case VOUCHER_LIST -> this.showVoucherList();
					case EXIT -> this.console.printInfoMessage(InfoMessageType.INVALID);
				}
			} catch (IllegalArgumentException e) {
				log.error("voucherCommandLine error", e);
				this.console.printInfoMessage(InfoMessageType.INVALID);
			}
		}
	}

	private void createVoucher() {
		this.console.printVoucherSelectMessage();
		var selectVoucherType = Integer.parseInt(console.getUserInput());
		var voucherType = VoucherType.findByNumber(selectVoucherType);

		this.console.printVoucherDiscountSelectMessage();
		var voucherValue = Integer.parseInt(this.console.getUserInput());
		this.voucherService.save(voucherType, voucherValue);
		this.console.print("success !");
	}

	private void showVoucherList() {
		List<VoucherDto.Response> vouchers = voucherService.getAll();

		if (vouchers.size() <= 0) {
			this.console.printInfoMessage(InfoMessageType.VOUCHER_EMPTY);

			return;
		}

		for (VoucherDto.Response voucher : vouchers) {
			System.out.println(voucher.toString());
		}
	}

	private List<String> getCommandDescription() {
		List<String> commandDescription = new ArrayList<>();
		Arrays.stream(VoucherCommandType.values())
			.forEach((v) -> commandDescription.add(v.getCommandMessage()));

		return commandDescription;
	}
}