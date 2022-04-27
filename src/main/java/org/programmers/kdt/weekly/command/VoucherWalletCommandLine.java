package org.programmers.kdt.weekly.command;

import org.programmers.kdt.weekly.command.io.Console;
import org.programmers.kdt.weekly.command.io.ErrorType;
import org.programmers.kdt.weekly.customer.model.Customer;
import org.programmers.kdt.weekly.customer.service.CustomerService;
import org.programmers.kdt.weekly.utils.UtilFunction;
import org.programmers.kdt.weekly.voucher.repository.VoucherRepository;
import org.programmers.kdt.weekly.voucherWallet.service.VoucherWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Component;

@Component
public class VoucherWalletCommandLine {

    private static final Logger logger = LoggerFactory.getLogger(VoucherWalletCommandLine.class);

    private final Console console;
    private final VoucherWalletService voucherWalletService;
    private final VoucherRepository voucherRepository;
    private final CustomerService customerService;

    public VoucherWalletCommandLine(Console console,
        VoucherWalletService voucherWalletService,
        VoucherRepository voucherRepository,
        CustomerService customerService) {
        this.console = console;
        this.voucherWalletService = voucherWalletService;
        this.voucherRepository = voucherRepository;
        this.customerService = customerService;
    }

    public void run() {
        var commandType = VoucherWalletCommandType.DEFAULT;
        var customer = createCustomer();

        while (commandType.isRunnable()) {
            this.console.printVoucherWalletCommand();
            var userInput = this.console.getUserInput();

            try {
                commandType = VoucherWalletCommandType.findByCommand(userInput);
            } catch (IllegalArgumentException e) {
                logger.debug("잘못된 사용자 입력 -> {}", userInput);
            }

            switch (commandType) {
                case WALLET_INSERT -> this.insertWallet(customer);
                case WALLET_LIST -> this.showWalletList(customer);
                case WALLET_DELETE -> this.deleteWallet(customer);
                case EXIT -> this.console.programExitMessage();
                default -> this.console.printErrorMessage(ErrorType.COMMAND);
            }
        }
    }

    private Customer createCustomer() {
        this.console.printInputMessage("email");
        var customerEmail = this.console.getUserInput();
        this.console.printInputMessage("name");
        var customerName = this.console.getUserInput();

        if (customerService.findByEmail(customerEmail).isPresent()) {
            return this.customerService.findByEmail(customerEmail).get();
        }
        var customer = this.customerService.create(customerEmail, customerName);
        this.console.printExecutionSuccessMessage();

        return customer;
    }

    private void showWalletList(Customer customer) {
        var voucherWallet = this.voucherWalletService.findById(customer.getCustomerId());
        if (voucherWallet.size() > 0) {
            voucherWallet.forEach(
                (voucher -> System.out.println(voucher.toString())));

            return;
        }
        this.console.printErrorMessage(ErrorType.VOUCHER_WALLET_EMPTY);
    }

    private void insertWallet(Customer customer) {
        var voucherList = this.voucherRepository.findAll();
        var userInput = "default";

        if (voucherList.size() <= 0) {
            console.printErrorMessage(ErrorType.VOUCHER_EMPTY);

            return;
        }
        voucherList.forEach((v) -> System.out.println(v.toString()));
        this.console.printInputMessage("voucher UUID");
        userInput = this.console.getUserInput();
        var maybeVoucherId = UtilFunction.validateUUID(userInput);

        if (maybeVoucherId.isPresent()) {
            try {
                this.voucherWalletService.insert(
                    customer.getCustomerId(), maybeVoucherId.get());
                this.console.printExecutionSuccessMessage();

                return;
            } catch (InvalidDataAccessApiUsageException e) {
                logger.error("voucherWallet insert error -> {}", e);
                System.out.println("저장에 실패");
            }
        }

        logger.debug("사용자 UUID 입력 에러 -> {}", userInput);
        this.console.printErrorMessage(ErrorType.VOUCHER_EMPTY);
    }

    private void deleteWallet(Customer customer) {
        this.console.printInputMessage("wallet UUID");
        var userInput = console.getUserInput();
        var maybeWalletId = UtilFunction.validateUUID(userInput);

        if (maybeWalletId.isPresent()) {
            this.voucherWalletService.deleteById(customer.getCustomerId(), maybeWalletId.get());
            this.console.printExecutionSuccessMessage();
        }

        logger.debug("사용자 UUID 입력 에러 -> {}", userInput);
        this.console.printErrorMessage(ErrorType.VOUCHER_EMPTY);
    }
}
