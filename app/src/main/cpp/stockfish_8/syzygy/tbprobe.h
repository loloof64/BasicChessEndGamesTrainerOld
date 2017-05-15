#ifndef TBPROBE_H
#define TBPROBE_H

#include "../search.h"
#include "tbcore.h"

namespace Tablebases {

extern int MaxCardinality;

void init(const std::string& path);
int probe_wdl(Position& pos, int *success);
int probe_dtz(Position& pos, int *success);
bool root_probe(Position& pos, Search::RootMoves& rootMoves, Value& score);
bool root_probe_wdl(Position& pos, Search::RootMoves& rootMoves, Value& score);
void filter_root_moves(Position& pos, Search::RootMoves& rootMoves);

}

uint64 calc_key_from_pcs(int *pcs, int mirror);

#endif
