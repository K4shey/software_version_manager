package net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec;

import java.util.List;

public record ConfigurationUpdateResponse(String configurationVersion, long size, String platformVersion,
                                          String updateInfoUrl, String howToUpdateInfoUrl, List<String> upgradeSequence,
                                          String programVersionUin, String supportEndDate) {

}
