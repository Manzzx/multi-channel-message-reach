package com.metax.web.vo;

import com.metax.web.vo.ReceiverRecords;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/19 9:30
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverRecordsPage {

    private List<ReceiverRecords> receiverRecords;

    private long total;
}
