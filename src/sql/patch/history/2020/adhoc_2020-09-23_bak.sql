-- 2020.09.24 质控更新 start
delete from audit_config;
delete from audit_summary;
delete from audit_module;
delete from audit_group;
delete from audit_link;
delete from audit_item;
delete from audit_file;


update audit_default_item set owner = '请填写模块负责人' where owner is null or owner = '';


update audit_default_item set system_item = true;


update audit_default_group set interpretation = '要成立专门的医疗设备和医用耗材管理委员会，明确委员会的工作职责，人员组成，要形成会议记录；委员会一般由医疗行政管理、医学工程、临床医学及护理、医院感染管理部门、医保、信息等专业人员组成，（如果委员会人员或者职责有调整，提供调整内容)，举证说明委员会的工作职责以及执行情况，比如2年内的重要设备购置预算领导意见和签字，购置设备的数据统计表；购置设备的汇总表；年度设备清单,购置手术器械汇总表，大购置论证会签到记录等' where version = '6.10.1.1.C.1';
update audit_default_group set interpretation = '根据医院实际情况配备合理的人员数量，根据设备生命周期管理的关键环节来分配关键岗位，一般分为行政/专技两种人员，制定岗位职责制度' where version = '6.10.1.1.C.2';
update audit_default_group set interpretation = '围绕着医疗装备的全生命周期，制定设备的管理制度，以及人员岗位制度等，详见模板。江苏版需要医院配备合理的维修人员与场地，对于有害作业需要有安全防护，在相关制度中要体现出来，在现场的维修场地也要注意设置防护措施。' where version = '6.10.1.1.C.3';
update audit_default_group set interpretation = '这里的主管部门（江苏版为医学装备管理部门）院外的涉及到若干个相关机构，如卫计委，质控中心（业务专家组成），市场监管局（特种设备管理），药监所等；医院内部主要包括党委会、院委会、人事处（考核）、设备耗材管理委员会；行政部（或院办，主要是红头文件或者市级下发各种文件，限于设备有关）等，可举证以上相关的主管部门对于设备相关工作和人员的检查和反馈，不局限于某个部门。' where version = '6.10.1.1.B.1';
update audit_default_group set interpretation = '举证说明，比如可以将市级的五年规划与院方的五年规划相结合，把其中和设备有关的预算项目进行举证，并详细说明有关的项目调整原因，是为了更好的配合临床需求。也可以举证说明过去2-3年中的设备发展规划中的改进亮点，从大的方向中证明医学装备规划在不断改进。' where version = '6.10.1.1.A.1';
update audit_default_group set interpretation = '该条可以先举证近年来的设备发展计划是什么，然后在其中挑选重要设备的采购论证和相关记录' where version = '6.10.1.2.C.1';
update audit_default_group set interpretation = '首先要制定详细的医学装备每个环节的制度与流程，（包括但不限于本条目的环节）每个环节的制度请参考模板，然后举证是如何执行的，关键信息要有，比如：维修工单记录，总的资产账目等。该环节可以用APM 资产云管家中的档案、维修、计量、报废等功能来进行相关工作的执行。' where version = '6.10.1.2.C.2';
update audit_default_group set interpretation = '临床使用部门要指定专门的设备管理人员，并提供与人员对应的设备使用培训和考核成绩记录。' where version = '6.10.1.2.C.3';
update audit_default_group set interpretation = '该处的主管部门主要是指与设备相关的上级行政组织，相关的医学装备近年来的监察记录都可以举例。' where version = '6.10.1.2.B.1';
update audit_default_group set interpretation = '主要是针对设备的配置规划和实行的过程进行改进，比如年度规划中的中期采购改进等。该处也可以向院务处或者财务处收集相关举证材料。' where version = '6.10.1.2.A.1';
update audit_default_group set interpretation = '50万的门槛可能会因地方政策要求而有所调整，该条主要是采购环节的论证，首先要制定相关制度，然后举例贵重医疗设备的可行性论证、申请过程的记录等' where version = '6.10.2.1.C.1';
update audit_default_group set interpretation = '国家和地方都有相关的分类方式，一般地方的分类要求比国家的68码要求更细，APM account中的设备档案分类综合了多方要求，完全可以建立充分的设备电子账目。' where version = '6.10.2.1.C.2';
update audit_default_group set interpretation = '大型设备的档案建立一般要求更高，证明文件比较齐全，比如CT、MR等，可以准备相应的招标文件、评标报告、投标书、验收报告、使用手册、注册证、培训记录、维修记录等相关文件。江苏省还要求严格实行甲乙类设备的配置证管理制度，因此要根据最新的甲乙类分类，保留相关设备的配置证文件。' where version = '6.10.2.1.C.3';
update audit_default_group set interpretation = '大型医用设备的评价方式目前还不完全统一，需要结合使用科室制定可量化的评价指标，具体指标可以参照模板。' where version = '6.10.2.1.C.4';
update audit_default_group set interpretation = '具体可以利用APM 资产云管家等系统的截图或表单导出来举证，如总设备台账、不同时间的设备验收清单、合同执行情况，多少台在正常使用中、多少台在维修中、多少台考虑报废、设备的维修工单记录、维修费用等' where version = '6.10.2.1.B.1';
update audit_default_group set interpretation = '该处的主管部门主要是指与设备相关的上级行政组织，比如市级监管部门的审计结果或者院办/财务等与设备相关的审计报告，提供给医学装备委员会' where version = '6.10.2.1.B.2';
update audit_default_group set interpretation = '要根据B中主管部门提供的审计报告中，对提出的问题进一步改进' where version = '6.10.2.1.A.1';
update audit_default_group set interpretation = '一定要有医疗器械临床使用安全事件监测与报告制度，尤其对于与设备相关的不良事件足够重视，拿出不良事件的上报记录。而且要有举措鼓励不良事件的上报。江苏版中额外要求：遵从安全操作使用流程，加强对装备警报提示系统的管理。 可以利用厂商对于大型设备的远程设备预警功能来管理，并且加强使用人员的操作安全的培训和考核。' where version = '6.10.3.1.C.1';
update audit_default_group set interpretation = '要有明确的计量设备管理制度和计量设备清单，并及时记录与跟进相关设备/耗材厂商的召回动作，即使在当前没有发生相关的质量安全事件，也要用文件说明当前未发生表示在密切监督设备的质量保障环节。' where version = '6.10.3.1.C.2';
update audit_default_group set interpretation = '对条款里提到的设备分类要制定相应的使用安全事件检测与报告制度，详见模板' where version = '6.10.3.1.C.3';
update audit_default_group set interpretation = '利用员工手册提醒员工注意自身安全，培训制度要通知到使用科室，让使用人员都知晓安全要求；另外设备管理人员也要掌握相应内容；' where version = '6.10.3.1.C.4';
update audit_default_group set interpretation = '可提供使用部门的大型设备日常维护自查表，医学装备部门要提供各个科室的使用安全总情况的考核表，' where version = '6.10.3.1.B.1';
update audit_default_group set interpretation = '该处的主管部门主要是指与设备相关的上级行政组织，比如省级质控中心提供过去检查的成绩，市场监督管理局检查的结果等' where version = '6.10.3.1.B.2';
update audit_default_group set interpretation = '医学装备管理部门要总结近年来设备使用安全与风险的总体情况，形式可以包括文档报告，相关表单等，在过去的使用安全问题中发现改进措施并执行。' where version = '6.10.3.1.A.1';
update audit_default_group set interpretation = '江苏版将6.10.3.2与6.10.3.3整合为一个款项，但是核心要求不变。该条主要提供核医学科装备、高压氧舱、放射类设备等的检验机构报告' where version = '6.10.3.2.C.1';
update audit_default_group set interpretation = '需要提供机房放射警示标签的照片' where version = '6.10.3.2.C.2';
update audit_default_group set interpretation = '要有设备使用人员对于放射与放疗类设备的安全培训制度，提供培训记录' where version = '6.10.3.2.C.3';
update audit_default_group set interpretation = '提供放射与放疗类机房环境的检查记录' where version = '6.10.3.2.B.1';
update audit_default_group set interpretation = '同6.10.3.2.C.1，主要通过第三方检验机构来监测安全情况' where version = '6.10.3.2.B.2';
update audit_default_group set interpretation = '主要是根据监管制度的不断完善来持续改进机房安全的管理' where version = '6.10.3.2.A.1';
update audit_default_group set interpretation = '需要准备特种装备的设备明细（包括分布位置），并且提供第三方检查机构提供的监测报告和合格证明。' where version = '6.10.3.3.C.1';
update audit_default_group set interpretation = '提供特殊装备操作人员的上岗证，培训记录等。' where version = '6.10.3.3.C.2';
update audit_default_group set interpretation = '提供医学装备部门对于特种装备的定期检查记录，可以利用APM 资产云管家进行周期性检查管理' where version = '6.10.3.3.B.1';
update audit_default_group set interpretation = '举证说明医学装备部门对于特殊装备的检查记录，并且形成阶段性总结' where version = '6.10.3.3.B.2';
update audit_default_group set interpretation = '要举证在近年来对于特种装备质量安全的治理案例，说明在不断改进中。' where version = '6.10.3.3.A.1';
update audit_default_group set interpretation = '江苏版是3.3；制定放射和诊断影像类设备的管理制度' where version = '6.10.3.4.C.1';
update audit_default_group set interpretation = '可以用各个厂商的维修报告来举证，另外可提供使用科室的日常自查表' where version = '6.10.3.4.C.2';
update audit_default_group set interpretation = '要先准备医院的放射类设备名称一览表，然后提供放射装置、放射工作场所、环境检测情况报告；年度放射诊疗频次表；介入放射学年度实施总人次数表；改进整改措施；特种设备使用登记证（包括检验报告）；计量测试技术研究院华东计量测试中心检定证书（压力表检定装置、锅炉）；液氧塔自查表；压力表减压器电接点表分部记录表；射线装置与同位素基本情况；' where version = '6.10.3.4.C.3';
update audit_default_group set interpretation = '医学装备部门可以提供模板，然后由使用科室提供自查记录；' where version = '6.10.3.4.B.1';
update audit_default_group set interpretation = '此处的主管部门指医学装备管理部门，要定期对放射和诊断性影像设备做预防性维护检查，提供（也可以由厂商提供相关服务）定期保养报告、计量或者质控报告等；' where version = '6.10.3.4.B.2';
update audit_default_group set interpretation = '需要做年度总结，对于关键使用指标要不断提出改进措施，详见模板。' where version = '6.10.3.4.A.1';
update audit_default_group set interpretation = '江苏版本是3.4；计量必须做台账/强检计量设备必须100%检测；非强检设备逐年增加，指定强检周期时间并落实；账目和强检数量不一致时，要注意查看是否是强检范围不够准确，另外要注意有些设备可能已经报废了；有部分科研设备也必须强检；' where version = '6.10.3.5.C.1';
update audit_default_group set interpretation = '统一设置计量设备清单，清单上所列计量设备必须进行检测；有两年内计量设备清单、定期检测记录和计量不合格设备的维修记录等相关资料' where version = '6.10.3.5.C.2';
update audit_default_group set interpretation = '现场查的时候一定看贴的有效期，原始检测报告都要保留记录，标志显示检测时间与登记记录一致。' where version = '6.10.3.5.C.3';
update audit_default_group set interpretation = '要有相关管理人员的培训和考核记录' where version = '6.10.3.5.C.4';
update audit_default_group set interpretation = '可以通过APM 护士助手来辅助使用科室对于计量设备定期自查，自行设置检查项和检查周期。' where version = '6.10.3.5.B.1';
update audit_default_group set interpretation = '可以通过APM 对计量设备定期巡检，自行设置检查项和检查周期。' where version = '6.10.3.5.B.2';
update audit_default_group set interpretation = '在允许的范围内，合理增加非强检的目录和品规' where version = '6.10.3.5.A.1';
update audit_default_group set interpretation = '江苏版把该条删了。现场抽查，所以要提前做好计量器具的合格标志检查。' where version = '6.10.3.5.A.2';
update audit_default_group set interpretation = '制定医疗仪器设备使用人员操作培训、考核制度与程序；提供培训会签到表、培训ppt（多放几种类型），测卷，考核结果汇总（不一定是成绩，也可以是签到记录）；' where version = '6.10.4.1.C.1';
update audit_default_group set interpretation = '技术支持可以用设备保养维护、维修记录等来体现；业务指导和咨询服务可以用设备培训记录表来体现；' where version = '6.10.4.1.C.2';
update audit_default_group set interpretation = '江苏把该条合并到C.1了。提供意外情况处理的培训PPT，另外要举证说明意外情况处理记录，如除颤仪意外情况处理方案表，呼吸机意外情况处理表，监护仪意外情况处理表，' where version = '6.10.4.1.C.3';
update audit_default_group set interpretation = '江苏版要求操作手册由装备管理部门管理和存档。主要部门尤其是急救设备需要有挂机本，需要涵盖简易操作手册，状态标识牌，检查维护的记录，医学转杯科也需要管理和存档相应资料，尤其是要准备呼吸机、除颤仪等急救类设备的。' where version = '6.10.4.1.C.4';
update audit_default_group set interpretation = '要提供医疗设备培训记录表（比如呼吸机，除颤仪、心电图机等），以及科室培训考核评分表' where version = '6.10.4.1.C.5';
update audit_default_group set interpretation = '要提供近来年科室培训情况与效果反馈表，以及使用科室内部的自查表（应知应会）' where version = '6.10.4.1.B.1';
update audit_default_group set interpretation = '对今年来设备使用培训情况进行总结，该处的主管部门指资产设备处，要体现培训了哪些内容，考核了哪些内容，哪些考核不够，应该再怎么处理。' where version = '6.10.4.1.B.2';
update audit_default_group set interpretation = '要对近年来整体的设备培训情况有详细分析，最好和日常的设备使用状况、维修状况、以及临床效益分析结合在一起，并且要提出实施有效的措施形成PDCA改进闭环。' where version = '6.10.4.1.A.1';
update audit_default_group set interpretation = '可以将所有的医学装备管理相关制度都整理在一起，另外要把全院设备按照科室配置、设备规划进行整理。江苏版还要求有临床使用记录，所以还要提供临床日常登记记录（开关机）' where version = '6.10.5.1.C.1';
update audit_default_group set interpretation = '首先要有医疗设备维修管理制度，然后可以利用 APM 保养、计量、维修等功能统一来管理相关设备，并且用护士助手来履行日常使用部门的保养和登记记录。' where version = '6.10.5.1.C.2';
update audit_default_group set interpretation = '江苏版多一条C.4: 应用信息化技术实施医学装备全流程的管理，有医学装备论证、决策、购置、验收、培训、使用、保养、维修、应用分析和更新、处置等相关工作的全程记录，信息真实、完整、准确。

对于医学装备的保障，指的是要充分保障设备的临床使用，一旦发生故障要能修，因此要把近年来所有的维修记录全部提供出来。对于江苏版的要求，可以利用APM来展示医学装备管理的验收、培训、使用、保养、维修、应用分析和更新、处置等相关工作。' where version = '6.10.5.1.C.3';
update audit_default_group set interpretation = '要举例说明对设备故障维修的分析情况，尤其是大型设备比如MRI/CT/DSA/MI设备，和急救类设备比如心电、监护仪等。里面要说明维修的总数量、故障原因、故障降低措施，或者设备规划' where version = '6.10.5.1.B.1';
update audit_default_group set interpretation = '可以整理安全和风险防控排查情况的总结，包括现在各环节存在的问题：设备质量、使用规范、监管力度等，并提出相应的整改措施，包括验收质量把关、加强培训、及时巡检等；' where version = '6.10.5.1.B.2';
update audit_default_group set interpretation = '江苏版要求“持续改进有成效，全院医学装备实现全生命周期信息化管理且管理规范，满足临床使用需求。”具体用数字或者信息化手段分析在哪些管理的KPI上有所改进，比如，做关于降低大型设备平均故障次数的分析，也可以做采购报修的原因分析，或者单次维修的原因分析等，' where version = '6.10.5.1.A.1';
update audit_default_group set interpretation = '使用科室要有仪器设备使用记录本，医学装备部门制定急救设备应急预案；15min之内必须要有好设备调过来，这个是必考项，应急储备设备列表，必须有；临床科室要求必须掌握发生急救设备故障处理流程表；' where version = '6.10.5.2.C.1';
update audit_default_group set interpretation = '若一旦故障停机，要及时张贴停用标识，不够用的情况下，要有调配机制，举证说明，比如呼吸机/心电监护等借用记录，' where version = '6.10.5.2.C.2';
update audit_default_group set interpretation = '江苏版要求应用信息化技术来进行实时监管。
可借鉴APM 资产云管家的调剂中心来完成，保证设备调配的实时性，一旦设备发生故障停机时，要第一时间张贴仪器损坏表或者撤回，保证投入临床使用的100%完好率。' where version = '6.10.5.2.B.1';
update audit_default_group set interpretation = '要有急救类、生命支持类装备完好率不同阶段的结果汇总，并提供不断改进完好率措施的举证，比如应急演练、各类设备的保养表、定期巡查清单、设备清点单等' where version = '6.10.5.2.A.1';
update audit_default_group set interpretation = '江苏版将6.10.5.3.C.1和6.10.5.3.C.2合二为一了，改为“有医学装备日常管理工作和意外应急管理等相关制度，建立应急管理程序，装备故障时有紧急替代流程。优先保障急救类、生命支持类装备的应急调配”。要制定医疗设备应急替代制度，提供应急储备设备列表，医疗设备应急替代工作流程。' where version = '6.10.5.3.C.1';
update audit_default_group set interpretation = '江苏版将6.10.5.3.C.1和6.10.5.3.C.2合二为一了，取消了6.10.5.3.C.2。要制定医疗设备应急替代制度，提供应急储备设备列表，医疗设备应急替代工作流程。' where version = '6.10.5.3.C.2';
update audit_default_group set interpretation = '对医务人员定期培训和考核医疗装备应急预案。' where version = '6.10.5.3.C.3';
update audit_default_group set interpretation = '要定期（建议每半年一次）举办医学装备应急演练，并提供应急设备日常调用情况的记录。' where version = '6.10.5.3.B.1';
update audit_default_group set interpretation = '对于应急演练要有分析与反馈，在设备调用方面要提供调用结果分析，不断改进应急储备库。' where version = '6.10.5.3.A.1';
update audit_default_group set interpretation = '制定关于医用耗材方面的制度，详见模板。' where version = '6.10.6.1.C.1';
update audit_default_group set interpretation = '江苏版多了一句要求：有高值医用耗材临床应用遴选、采购、储存、使用、监测、评价、监督等管理的程序，并有记录。 集中举证某类医疗耗材的入库、出库、手术使用登记、手术相关使用记录、知情同意书等，有一个完整的追溯和监督记录。' where version = '6.10.6.1.C.2';
update audit_default_group set interpretation = '要保留高值耗材的供应商营业执照、经营许可证、医疗器械注册证；第二类医疗器械经营备案凭证，供应商的授权书；植入性医疗器械使用登记表；要分2步：1，使用范围证明；2，使用范围相关记录' where version = '6.10.6.1.C.3';
update audit_default_group set interpretation = '制定医疗器械不良事件监测报告管理办法，包括医疗安全不良事件上报流程、可疑医疗器械不良事件报告表模板等；提供上报实例，在医用耗材管理委员会以外，如果院方举办或参与过医疗耗材不良事件的汇报分析和整改，请一并提供，从而证明院方对于耗材不良事件的重视程度。' where version = '6.10.6.1.C.4';
update audit_default_group set interpretation = '提供不同科室的高值耗材自查表，并附有科室负责人签名。' where version = '6.10.6.1.B.1';
update audit_default_group set interpretation = '提供院方定期巡查的整改建议，若有，请提供院方审计科对于医用耗材的采购使用分析，说明为何要购入该类医用耗材，是否符合耗材管理制度药材。' where version = '6.10.6.1.B.2';
update audit_default_group set interpretation = '江苏版将“百元医疗收入(不含药品收入)中消耗的卫生材料降到20元以下”删掉了。材占比是医院考核KPI之一，可向院务处或者财务处收集院方的材占比分析报告。' where version = '6.10.6.1.A.1';
update audit_default_group set interpretation = '江苏版把6.10.7.1与6.10.7.2合并了，核心内容保留，更加简洁。
主要针对医学装备处的行政管理和技术人员进行培训，准备相关学分证明、相关技能培训证书等' where version = '6.10.7.1.C.1';
update audit_default_group set interpretation = '对大型设备举证说明，包括使用情况，设备计划、维修年度总结、报废小结等，来说明医学装备处对于发展规划的落实情况。' where version = '6.10.7.1.C.2';
update audit_default_group set interpretation = '举证说明安全管理的工作内容和整改反馈，详见模板' where version = '6.10.7.1.B.1';
update audit_default_group set interpretation = '可以借助APM资产云管家按周期进行设备巡查，定期总结使用安全情况。' where version = '6.10.7.1.B.2';
update audit_default_group set interpretation = '要定期对使用科室的使用满意度调查，帮助医学装备管理部门规范设备的规划和管理，加强使用科室的培训和支持制度，持续改进使用满意度。' where version = '6.10.7.1.A.1';
update audit_default_group set interpretation = '江苏版把6.10.7.1与6.10.7.2合并了，核心内容保留，更加简洁。首先要有非常健全的设备全周期管理制度，包括使用科室对于医疗设备的管理制度。' where version = '6.10.7.2.C.1';
update audit_default_group set interpretation = '要提供详细的应急程序制度，包括应急替代制度、应急物资管理制度等。对于医学装备的日常管理可以用大型医疗设备的采购记录来举证。' where version = '6.10.7.2.C.2';
update audit_default_group set interpretation = '举证说明：贵重设备的培训记录，以及使用人员的上岗证。' where version = '6.10.7.2.C.3';
update audit_default_group set interpretation = '可以利用APM资产云管家管理维护保养，提供相应的保养和维修报告，使用科室可以利用护士助手进行日常检查。另外对科室的设备分布情况要有明细。' where version = '6.10.7.2.B.1';
update audit_default_group set interpretation = '该条设备装备管理部门是主管部门，提供对安全检查的调研表和巡检表' where version = '6.10.7.2.B.2';
update audit_default_group set interpretation = '每一年要比前一年做的更好，制度任何一点改变都需要写到总章程里，拔一个高度，但是不要矫枉过正，把握改进的度。' where version = '6.10.7.2.A.1';


INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(91, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 1, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(92, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 2, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(93, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 3, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(94, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 4, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(95, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 5, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(96, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 6, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(97, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 7, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(98, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 8, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(99, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 9, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(100, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 10, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(101, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 11, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(102, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 12, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(103, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 13, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(104, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 14, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(105, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 15, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(106, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 16, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(107, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 17, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(108, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 18, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(109, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 19, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(110, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 20, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(111, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 21, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(112, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 22, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(113, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 23, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(114, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 24, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(115, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 25, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(116, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 26, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(117, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 27, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(118, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 28, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(119, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 29, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(120, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 30, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(121, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 31, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(122, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 32, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(123, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 33, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(124, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 34, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(125, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 35, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(126, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 36, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(127, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 37, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(128, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 38, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(129, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 39, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(130, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 40, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(131, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 41, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(132, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 42, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(133, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 43, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(134, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 44, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(135, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 45, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(136, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 46, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(137, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 47, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(138, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 48, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(139, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 49, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(140, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 50, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(141, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 51, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(142, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 52, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(143, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 53, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(144, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 54, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(145, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 55, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(146, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 56, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(147, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 57, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(148, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 58, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(149, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 59, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(150, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 60, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(151, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 61, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(152, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 62, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(153, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 63, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(154, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 64, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(155, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 65, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(156, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 66, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(157, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 67, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(158, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 68, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(159, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 69, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(160, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 70, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(161, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 71, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(162, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 72, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(163, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 73, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(164, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 74, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(165, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 75, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(166, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 76, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(167, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 77, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(168, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 78, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(169, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 79, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(170, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 80, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(171, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 81, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(172, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 82, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(173, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 83, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(174, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 84, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(175, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 85, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(176, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 86, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(177, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 87, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(178, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 88, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(179, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 89, '自查文件', 2, '请填写模块负责人', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(180, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 90, '自查文件', 2, '请填写模块负责人', false, false);

-- 2020.09.24 质控更新 end