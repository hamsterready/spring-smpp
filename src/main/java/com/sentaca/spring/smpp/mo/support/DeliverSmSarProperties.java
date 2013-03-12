package com.sentaca.spring.smpp.mo.support;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.OptionalParameter.Tag;

public class DeliverSmSarProperties {

  private short msgRefNo = 0;
  private byte msgMaxNum = 0;
  private byte msgSeqNum = 0;

  public DeliverSmSarProperties(DeliverSm deliverSm) {
    if (deliverSm.getOptionalParametes() != null) {
      for (OptionalParameter param : deliverSm.getOptionalParametes()) {
        switch (Tag.valueOf(param.tag)) {
        case SAR_MSG_REF_NUM:
          msgRefNo = ((OptionalParameter.Short) param).getValue();
          break;
        case SAR_TOTAL_SEGMENTS:
          msgMaxNum = ((OptionalParameter.Byte) param).getValue();
          break;
        case SAR_SEGMENT_SEQNUM:
          msgSeqNum = ((OptionalParameter.Byte) param).getValue();
          break;
        default:
          break;
        }
      }
    }
  }

  public byte getMsgMaxNum() {
    return msgMaxNum;
  }

  public short getMsgRefNo() {
    return msgRefNo;
  }

  public byte getMsgSeqNum() {
    return msgSeqNum;
  }

  public boolean isStandalone() {
    return msgSeqNum == 0 && msgRefNo == 0 && msgMaxNum == 0;
  }

  public void setMsgMaxNum(byte msgMaxNum) {
    this.msgMaxNum = msgMaxNum;
  }

  public void setMsgRefNo(short msgRefNo) {
    this.msgRefNo = msgRefNo;
  }

  public void setMsgSeqNum(byte msgSeqNum) {
    this.msgSeqNum = msgSeqNum;
  }
}
