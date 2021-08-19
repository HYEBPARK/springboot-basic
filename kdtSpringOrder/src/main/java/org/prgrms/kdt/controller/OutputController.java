package org.prgrms.kdt.controller;

import org.prgrms.kdt.domain.voucher.Voucher;
import org.prgrms.kdt.service.VoucherService;

import java.util.List;

public class OutputController {

    public void showVoucherList(VoucherService voucherService) {
        List<Voucher> voucherList = voucherService.getAllVouchers();
        if(voucherList.isEmpty()) {
            System.out.println("등록된 바우처가 없습니다.");
        } else {
            voucherList.forEach(System.out::println);
        }
    }

}