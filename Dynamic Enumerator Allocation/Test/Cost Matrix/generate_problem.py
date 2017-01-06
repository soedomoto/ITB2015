import os

import numpy
import unicodecsv as csv


input = 'coord_nagari_2014.csv'
output = 'enumeration_problem.csv'

ruta_count = 10
lambda_interview_time = 1800
# Based on (Sudman, 1965) : inter-node within segment = 15% time, interview = 37% time
lambda_internode_time = (float(15)/37) * (float(ruta_count-1)/ruta_count) * lambda_interview_time


input_file = open(os.path.join(os.pardir, 'Data', input), 'r')
output_file = open(os.path.join(os.pardir, 'Data', output), 'wb')

# Write header
csv.writer(output_file, encoding='utf-8').writerow(('id', 'lat', 'lon', 'service_time (sec)'))
output_file.flush()

for id, lat, lon in list(csv.reader(input_file, encoding='utf-8'))[1:]:
    ruta_service_times = numpy.random.poisson(lam=lambda_interview_time, size=ruta_count)
    inter_rute_times = numpy.random.poisson(lam=lambda_internode_time, size=ruta_count-1)
    segment_service_time = sum(ruta_service_times) + sum(inter_rute_times)

    csv.writer(output_file, encoding='utf-8').writerow((id, lat, lon, segment_service_time))
    output_file.flush()
