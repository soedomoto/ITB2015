import statistics as statistics
import unicodecsv as csv

def column(matrix, i):
    return [row[i] for row in matrix]


table = csv.reader(open('/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/Research/Dynamic Enumerator Allocation/Test/VRP/output/mtsp_dynamic_time_windows.csv'))
table = list(table)
table = table[1:]

routes = {}
for enumerator, act, loc, lat, lon, time, start, stop, cost in table:
    routes.setdefault(enumerator, [])
    routes[enumerator].append((loc, float(start), float(stop)))

table = csv.reader(open('/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/Research/Dynamic Enumerator Allocation/Test/Data/enumeration_problem.csv'))
table = list(table)
table = table[1:]

locations = {}
for id, lat, lon, service_time in table:
    print '{} & {}\\\\'.format(id, service_time)
    locations[id] = (lat, lon, int(service_time))

table = csv.reader(open('/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/Research/Dynamic Enumerator Allocation/Test/Data/cost_matrix_nagari_2014.csv'))
table = list(table)
table = table[1:]

cost_matrix = {}
for id_a, id_b, distance, duration, status in table:
    cost_matrix.setdefault(id_a, {})
    cost_matrix[id_a][id_b] = (float(distance), float(duration))
    cost_matrix.setdefault(id_b, {})
    cost_matrix[id_b][id_a] = (float(distance), float(duration))


enumerator_total_time = []
for id, jobs in routes.items():
    sorted(jobs, key=lambda x: x[1], reverse=False)

    total_durations = 0.0
    last_loc = None
    for loc, start, stop in jobs:
        service_time = locations[loc][2]
        route_duration = cost_matrix[last_loc][loc][1] if last_loc else 0
        total_durations += route_duration + service_time

        last_loc = loc

    enumerator_total_time.append((id, total_durations/3600))
    print id, ':', ' --> '.join(column(jobs, 0))

print ''
for id, total in enumerator_total_time:
    print '{0} & {1:.3g}\\\\'.format(id, total)

print ''
print 'mean = ', float(sum(column(enumerator_total_time, 1))) / len(column(enumerator_total_time, 1))
print 'stdev = ', statistics.stdev(column(enumerator_total_time, 1))
