import pycosat


def parse_clauses_from_file():
    with open('../../dimacs/dimacs_4-colouring_1510037543766.txt') as f:
        first_line = f.readline()
        var_count, clause_count = [int(x) for x in first_line.split()[2:4]]
        clauses = []
        for line in f:  # read rest of lines
            clauses.append([int(x) for x in line.split()])
        assert clause_count == len(clauses)
        return clauses


def main():
    clauses = parse_clauses_from_file()
    print(pycosat.solve(clauses))


main()
