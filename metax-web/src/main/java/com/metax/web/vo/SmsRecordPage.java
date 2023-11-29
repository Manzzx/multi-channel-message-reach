package com.metax.web.vo;

import com.metax.web.vo.SmsRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/30 23:11
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRecordPage {

   private List<SmsRecord> smsRecords;

   private long total;

}
