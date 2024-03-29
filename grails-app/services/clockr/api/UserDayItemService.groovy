package clockr.api

import clockr.api.DayItem.DayItemType
import grails.gorm.transactions.Transactional

import java.time.LocalDate
import java.time.ZoneId

@Transactional
class UserDayItemService {

    Integer countDayItemsByTypeForMonth(Long userId, DayItemType type, Integer year, Integer month) {
        DayItem[] dayItems = DayItem.findAllByUserAndType(User.get(userId), type)
        LocalDate targetStart = LocalDate.of(year, month, 1)
        LocalDate targetEnd = targetStart.withDayOfMonth(targetStart.lengthOfMonth()).plusDays(1)

        return dayItems?.findAll { dayItem ->
            LocalDate date = dayItem.day.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
            return date >= targetStart && date < targetEnd
        }?.size()
    }

    Integer countDayItemsByTypeForMonthAndContract(Long userId, Long contractId, DayItemType type, Integer year, Integer month) {
        DayItem[] dayItems = DayItem.findAllByUserAndType(User.get(userId), type)
        Contract contract = Contract.get(contractId)

        LocalDate localContractStartAt = contract.startAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
        LocalDate localContractEndAt = contract.endAt ? contract.endAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate() : null

        LocalDate targetStart = LocalDate.of(year, month, 1)
        if (localContractStartAt.isAfter(targetStart)) {
            targetStart = LocalDate.of(year, month, localContractStartAt.getDayOfMonth())
        }

        LocalDate targetEnd = targetStart.withDayOfMonth(targetStart.lengthOfMonth()).plusDays(1)
        if (localContractEndAt && localContractEndAt.isBefore(targetEnd)) {
            targetEnd = LocalDate.of(year, month, localContractEndAt.getDayOfMonth()).plusDays(1)
        }

        return dayItems?.findAll { dayItem ->
            LocalDate date = dayItem.day.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
            return date >= targetStart && date < targetEnd
        }?.size()
    }

    Integer countDayItemsByTypeForYear(Long userId, DayItemType type, Integer year) {
        DayItem[] dayItems = DayItem.findAllByUserAndType(User.get(userId), type)
        LocalDate targetStart = LocalDate.of(year, 1, 1)
        LocalDate targetEnd = targetStart.plusYears(1)

        return dayItems?.findAll { dayItem ->
            LocalDate date = dayItem.day.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
            return date >= targetStart && date < targetEnd
        }?.size()
    }

    DayItem getDayItemByTypeForDay(Long userId, DayItemType type, LocalDate day) {
        DayItem[] dayItems = DayItem.findAllByUserAndType(User.get(userId), type)
        return dayItems?.find { dayItem -> dayItem.day.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate() == day }
    }
}
