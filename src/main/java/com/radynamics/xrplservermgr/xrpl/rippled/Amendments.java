package com.radynamics.xrplservermgr.xrpl.rippled;

import com.radynamics.xrplservermgr.xrpl.Amendment;
import com.vdurmont.semver4j.Semver;

import java.util.ArrayList;
import java.util.List;

public class Amendments {
    public static List<Amendment> all() {
        var list = new ArrayList<Amendment>();
        list.add(Amendment.of("96FD2F293A519AE1DB6F8BED23E4AD9119342DA7CB6BAFD00953D16C54205D8B", "PriceOracle", new Semver("2.2.0")));
        list.add(Amendment.of("755C971C29971C9F20C6F080F2ED96F87884E40AD19554A5EBECDCEC8A1F77FE", "fixEmptyDID", new Semver("2.2.0")));
        list.add(Amendment.of("2BF037D90E1B676B17592A8AF55E88DB465398B4B597AE46EECEE1399AB05699", "fixXChainRewardRounding", new Semver("2.2.0")));
        list.add(Amendment.of("7BB62DC13EC72B775091E9C71BF8CF97E122647693B50C5E87A80DFD6FCFAC50", "fixPreviousTxnID", new Semver("2.2.0")));
        list.add(Amendment.of("35291ADD2D79EB6991343BDA0912269C817D0F094B02226C1C14AD2858962ED4", "fixAMMv1_1", new Semver("2.2.0")));
        list.add(Amendment.of("12523DF04B553A0B1AD74F42DDB741DE8DC06A03FC089A0EF197E2A87F1D8107", "fixAMMOverflowOffer", new Semver("2.1.1")));
        list.add(Amendment.of("C393B3AEEBF575E475F0C60D5E4241B2070CC4D0EB6C4846B1A07508FAEFC485", "fixInnerObjTemplate", new Semver("2.1.0")));
        list.add(Amendment.of("03BDC0099C4E14163ADA272C1B6F6FABB448CC3E51F522F978041E4B57D9158C", "fixNFTokenReserve", new Semver("2.1.0")));
        list.add(Amendment.of("DB432C3A09D9D5DFC7859F39AE5FF767ABC59AED0A9FB441E83B814D8946C109", "DID", new Semver("2.0.0")));
        list.add(Amendment.of("C98D98EE9616ACD36E81FDEB8D41D349BF5F1B41DD64A0ABC1FE9AA5EA267E9C", "XChainBridge", new Semver("2.0.0")));
        list.add(Amendment.of("15D61F0C6DB6A2F86BCF96F1E2444FEC54E705923339EC175BD3E517C8B3FF91", "fixDisallowIncomingV1", new Semver("2.0.0")));
        list.add(Amendment.of("3318EA0CF0755AF15DAC19F2B5C5BCBFF4B78BDD57609ACCAABE2C41309B051A", "fixFillOrKill", new Semver("2.0.0")));
        list.add(Amendment.of("56B241D7A43D40354D02A9DC4C8DF5C7A1F930D92A9035C4E12291B3CA3E1C2B", "Clawback", new Semver("1.12.0")));
        list.add(Amendment.of("8CC0774A3BF66D1D22E76BBDA8E8A232E6B6313834301B3B23E8601196AE6455", "AMM", new Semver("1.12.0")));
        list.add(Amendment.of("27CD95EE8E1E5A537FF2F89B6CEB7C622E78E9374EBD7DCBEDFAE21CD6F16E0A", "fixReducedOffersV1", new Semver("1.12.0")));
        list.add(Amendment.of("AE35ABDEFBDE520372B31C957020B34A7A4A9DC3115A69803A44016477C84D6E", "fixNFTokenRemint", new Semver("1.11.0")));
        list.add(Amendment.of("93E516234E35E08CA689FA33A6D38E103881F8DCB53023F728C307AA89D515A7", "XRPFees", new Semver("1.10.0")));
        list.add(Amendment.of("47C3002ABA31628447E8E9A8B315FAA935CE30183F9A9B86845E469CA2CDC3DF", "DisallowIncoming", new Semver("1.10.0")));
        list.add(Amendment.of("73761231F7F3D94EC3D8C63D91BDD0D89045C6F71B917D1925C01253515A6669", "fixNonFungibleTokensV1_2", new Semver("1.10.0")));
        list.add(Amendment.of("F1ED6B4A411D8B872E65B9DCB4C8B100375B0DD3D62D07192E011D6D7F339013", "fixTrustLinesToSelf", new Semver("1.10.0")));
        list.add(Amendment.of("2E2FB9CF8A44EB80F4694D38AADAE9B8B7ADAFD2F092E10068E61C98C4F092B0", "fixUniversalNumber", new Semver("1.10.0")));
        list.add(Amendment.of("75A7E01C505DD5A179DFE3E000A9B6F1EDDEB55A12F95579A23E15B15DC8BE5A", "ImmediateOfferKilled", new Semver("1.10.0")));
        list.add(Amendment.of("DF8B4536989BDACE3F934F29423848B9F1D76D09BE6A1FCFE7E7F06AA26ABEAD", "fixRemoveNFTokenAutoTrustLine", new Semver("1.9.4")));
        list.add(Amendment.of("32A122F1352A4C7B3A6D790362CC34749C5E57FCE896377BFDC6CCD14F6CD627", "NonFungibleTokensV1_1", new Semver("1.9.2")));
        list.add(Amendment.of("36799EA497B1369B170805C078AEFE6188345F9B3E324C21E9CA3FF574E3C3D6", "fixNFTokenNegOffer", new Semver("1.9.2")));
        list.add(Amendment.of("B2A4DB846F0891BF2C76AB2F2ACC8F5B4EC64437135C6E56F3F859DE5FFD5856", "ExpandedSignerList", new Semver("1.9.1")));
        list.add(Amendment.of("0285B7E5E08E1A8E4C15636F0591D87F73CB6A7B6452A932AD72BBC8E5D1CBE3", "fixNFTokenDirV1", new Semver("1.9.1")));
        list.add(Amendment.of("3C43D9A973AA4443EF3FC38E42DD306160FBFFDAB901CD8BAA15D09F2597EB87", "NonFungibleTokensV1", new Semver("1.9.0")));
        list.add(Amendment.of("98DECF327BF79997AEC178323AD51A830E457BFC6D454DAF3E46E5EC42DC619F", "CheckCashMakesTrustLine", new Semver("1.8.0")));
        list.add(Amendment.of("B4E4F5D2D6FB84DF7399960A732309C9FD530EAE5941838160042833625A6076", "NegativeUNL", new Semver("1.7.3")));
        list.add(Amendment.of("B6B3EEDC0267AB50491FDC450A398AF30DBCD977CECED8BEF2499CAB5DAC19E2", "fixRmSmallIncreasedQOffers", new Semver("1.7.2")));
        list.add(Amendment.of("452F5906C46D46F407883344BFDD90E672B672C5E9943DB4891E3A34FEEEB9DB", "fixSTAmountCanonicalize", new Semver("1.7.0")));
        list.add(Amendment.of("955DF3FA5891195A9DAEFA1DDC6BB244B545DDE1BAA84CBB25D5F12A8DA68A0C", "TicketBatch", new Semver("1.7.0")));
        list.add(Amendment.of("AF8DF7465C338AE64B1E937D6C8DA138C0D63AD5134A68792BBBE1F63356C422", "FlowSortStrands", new Semver("1.7.0")));
        list.add(Amendment.of("1F4AFA8FA1BC8827AD4C0F682C03A8B671DCDF6B5C4DE36D44243A684103EF88", "HardenedValidations", new Semver("1.6.0")));
        list.add(Amendment.of("25BA44241B3BD880770BFA4DA21C7180576831855368CBEC6A3154FDE4A7676E", "fix1781", new Semver("1.6.0")));
        list.add(Amendment.of("4F46DF03559967AC60F2EB272FEFE3928A7594A45FF774B87A7E540DB0F8F068", "fixAmendmentMajorityCalc", new Semver("1.6.0")));
        list.add(Amendment.of("89308AF3B8B10B7192C4E613E1D2E4D9BA64B2EE2D5232402AE82A6A7220D953", "fixQualityUpperBound", new Semver("1.5.0")));
        list.add(Amendment.of("00C1FC4A53E60AB02C864641002B3172F38677E29C26C5406685179B37E1EDAC", "RequireFullyCanonicalSig", new Semver("1.5.0")));
        list.add(Amendment.of("30CD365592B8EE40489BA01AE2F7555CAC9C983145871DC82A42A31CF5BAE7D9", "DeletableAccounts", new Semver("1.4.0")));
        list.add(Amendment.of("8F81B066ED20DAECA20DF57187767685EEF3980B228E0667A650BAF24426D3B4", "fixCheckThreading", new Semver("1.4.0")));
        list.add(Amendment.of("621A0B264970359869E3C0363A899909AAB7A887C8B73519E4ECF952D33258A8", "fixPayChanRecipientOwnerDir", new Semver("1.4.0")));
        list.add(Amendment.of("C4483A1896170C66C098DEA5B0E024309C60DC960DE5F01CD7AF986AA3D9AD37", "fixMasterKeyAsRegularKey", new Semver("1.3.1")));
        list.add(Amendment.of("2CD5286D8D687E98B41102BDD797198E81EA41DF7BD104E6561FEB104EFF2561", "fixTakerDryOfferRemoval", new Semver("1.2.0")));
        list.add(Amendment.of("586480873651E106F1D6339B0C4A8945BA705A777F3F4524626FF1FC07EFE41D", "MultiSignReserve", new Semver("1.2.0")));
        list.add(Amendment.of("FBD513F1B893AC765B78F250E6FFA6A11B573209D1842ADC787C850696741288", "fix1578", new Semver("1.2.0")));
        list.add(Amendment.of("3CBC5C4E630A1B82380295CDA84B32B49DD066602E74E39B85EF64137FA65194", "DepositPreauth", new Semver("1.1.0")));
        list.add(Amendment.of("5D08145F0A4983F23AFFFF514E83FAD355C5ABFBB6CAB76FB5BC8519FF5F33BE", "fix1515", new Semver("1.1.0")));
        list.add(Amendment.of("7117E2EC2DBF119CA55181D69819F1999ECEE1A0225A7FD2B9ED47940968479C", "fix1571", new Semver("1.0.0")));
        list.add(Amendment.of("58BE9B5968C4DA7C59BA900961828B113E5490699B21877DEF9A31E9D0FE5D5F", "fix1623", new Semver("1.0.0")));
        list.add(Amendment.of("CA7C02118BA27599528543DFE77BA6838D1B0F43B447D4D7F53523CE6A0E9AC2", "fix1543", new Semver("1.0.0")));
        list.add(Amendment.of("157D2D480E006395B76F948E3E07A45A05FE10230D88A7993C71F97AE4B1F2D1", "Checks", new Semver("0.90.0")));
        list.add(Amendment.of("67A34F2CF55BFC0F93AACD5B281413176FEE195269FA6D95219A2DF738671172", "fix1513", new Semver("0.90.0")));
        list.add(Amendment.of("F64E1EABBE79D55B3BB82020516CEC2C582A98A6BFE20FBE9BB6A0D233418064", "DepositAuth", new Semver("0.90.0")));
        list.add(Amendment.of("1D3463A5891F9E589C5AE839FFAC4A917CE96197098A1EF22304E1BC5B98A454", "fix1528", new Semver("0.80.0")));
        list.add(Amendment.of("B9E739B8296B4A1BB29BE990B17D66E21B62A300A909F25AC55C22D6C72E1F9D", "fix1523", new Semver("0.80.0")));
        list.add(Amendment.of("6C92211186613F9647A89DFFBAB8F94C99D4C7E956D495270789128569177DA1", "fix1512", new Semver("0.80.0")));
        list.add(Amendment.of("B4D44CC3111ADD964E846FC57760C8B50FFCD5A82C86A72756F6B058DDDF96AD", "fix1201", new Semver("0.80.0")));
        list.add(Amendment.of("CC5ABAE4F3EC92E94A59B1908C2BE82D2228B6485C00AFF8F22DF930D89C194E", "SortedDirectories", new Semver("0.80.0")));
        list.add(Amendment.of("42EEA5E28A97824821D4EF97081FE36A54E9593C6E4F20CBAE098C69D2E072DC", "fix1373", new Semver("0.70.0")));
        list.add(Amendment.of("3012E8230864E95A58C60FD61430D7E1B4D3353195F2981DC12B0C7C0950FFAC", "FlowCross", new Semver("0.70.0")));
        list.add(Amendment.of("DC9CA96AEA1DCF83E527D1AFC916EFAF5D27388ECA4060A88817C1238CAEE0BF", "EnforceInvariants", new Semver("0.70.0")));
        list.add(Amendment.of("07D43DCE529B15A10827E5E04943B496762F9A88E3268269D69C44BE49E21104", "Escrow", new Semver("0.60.0")));
        list.add(Amendment.of("E2E6F2866106419B88C50045ACE96368558C345566AC8F2BDF5A5B5587F0E6FA", "fix1368", new Semver("0.60.0")));
        list.add(Amendment.of("86E83A7D2ECE3AD5FA87AB2195AE015C950469ABF0B72EAACED318F74886AE90", "CryptoConditionsSuite", new Semver("0.60.0")));
        list.add(Amendment.of("1562511F573A19AE9BD103B5D6B9E01B3B46805AEC5D3C4805C902B514399146", "CryptoConditions", new Semver("0.50.0")));
        list.add(Amendment.of("532651B4FD58DF8922A49BA101AB3E996E5BFBF95A913B3E392504863E63B164", "TickSize", new Semver("0.50.0")));
        list.add(Amendment.of("08DE7D96082187F6E6578530258C77FAABABE4C20474BDB82F04B021F1A68647", "PayChan", new Semver("0.33.0")));
        list.add(Amendment.of("740352F2412A9909880C23A559FCECEDA3BE2126FED62FC7660D628A06927F11", "Flow", new Semver("0.33.0")));
        list.add(Amendment.of("42426C4D4F1009EE67080A9B7965B44656D7714D104A72F9B4369F97ABF044EE", "FeeEscalation", new Semver("0.31.0")));
        list.add(Amendment.of("4C97EBA926031A7CF7D7B36FDE3ED66DDA5421192D63DE53FFB46E43B9DC8373", "MultiSign", new Semver("0.31.0")));
        list.add(Amendment.of("6781F8368C4771B83E8B821D88F580202BCB4228075297B19E4FDC5233F1EFDC", "TrustSetAuth", new Semver("0.30.0")));
        return list;
    }
}
