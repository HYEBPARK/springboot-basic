package org.programmers.kdt.weekly.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.programmers.kdt.weekly.command.io.Console;
import org.programmers.kdt.weekly.command.io.InfoMessageType;
import org.programmers.kdt.weekly.voucher.model.Voucher;
import org.programmers.kdt.weekly.voucher.model.VoucherType;
import org.programmers.kdt.weekly.voucher.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VoucherCommandLine {

    private static final Logger logger = LoggerFactory.getLogger(VoucherCommandLine.class);

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
                }
            } catch (IllegalArgumentException e) {
                logger.error("voucherCommandLine error -> {}", e);
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
        this.voucherService.create(UUID.randomUUID(), voucherType, voucherValue);
        this.console.print("success !");
    }

    private void showVoucherList() {
        List<Voucher> vouchers = voucherService.getVouchers();

        if (vouchers.size() <= 0) {
            this.console.printInfoMessage(InfoMessageType.VOUCHER_EMPTY);

            return;
        }
        vouchers.forEach((v) -> System.out.println(v.toString()));
    }

    private List<String> getCommandDescription() {
        List<String> commandDescription = new ArrayList<>();
        Arrays.stream(VoucherCommandType.values())
            .forEach((v) -> commandDescription.add(v.getCommandMessage()));

        return commandDescription;
    }
}