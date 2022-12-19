export interface Game {
  bggId?: number;
  name?: string;
  description?: string;
  thumbnailUrl?: string;
  imageUrl?: string;
  minPlayers?: number;
  maxPlayers?: number;
  yearPublished?: number;
  publisher?: string;
  designer?: string;
  artist?: string;
  honors?: string;
  bestPlayers?: number[];
  goodPlayers?: number[];
  avgRating?: number;
  avgWeight?: number;
}
