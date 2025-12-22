import { useCallback, useMemo } from 'react';
import { useSearchParams } from 'react-router-dom';
import { sellersApi } from '../api/sellers';
import EmptyState from '../components/EmptyState';
import FetchBoundary from '../components/FetchBoundary';
import PageHeader from '../components/PageHeader';
import Pagination from '../components/Pagination';
import SellerCard from '../components/SellerCard';
import SellerFilters from '../components/SellerFilters';
import { useFetch } from '../hooks/useFetch';

const FILTER_KEYS = ['gameName', 'minRating', 'maxRating'];
const PAGE_SIZE = 10;

function toSearchParams(filters, page) {
  const entries = Object.entries({ ...filters, page: page > 0 ? page : '' });
  return Object.fromEntries(entries.filter(([, value]) => value !== ''));
}

export default function TopSellersPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  const filters = useMemo(
    () => Object.fromEntries(FILTER_KEYS.map((key) => [key, searchParams.get(key) ?? ''])),
    [searchParams],
  );
  const page = Number(searchParams.get('page') ?? 0);

  const fetchSellers = useCallback(
    (signal) => sellersApi.getAll({ ...filters, page, size: PAGE_SIZE, sort: 'rating,desc' }, signal),
    [filters, page],
  );
  const { data, loading, error } = useFetch(fetchSellers);

  const sellers = data?.content ?? [];
  const totalPages = data?.page?.totalPages ?? 0;

  const handleFiltersChange = (nextFilters) => setSearchParams(toSearchParams(nextFilters, 0), { replace: true });
  const handlePageChange = (nextPage) => setSearchParams(toSearchParams(filters, nextPage));

  return (
    <>
      <PageHeader title="Top sellers" subtitle="Ratings are built from comments verified by our moderators." />
      <SellerFilters filters={filters} onChange={handleFiltersChange} onReset={() => setSearchParams({})} />
      <FetchBoundary loading={loading} error={error}>
        {sellers.length > 0 ? (
          <>
            <ol className="list">
              {sellers.map((seller, index) => (
                <li key={seller.id}>
                  <SellerCard seller={seller} position={page * PAGE_SIZE + index + 1} />
                </li>
              ))}
            </ol>
            <Pagination page={page} totalPages={totalPages} onChange={handlePageChange} />
          </>
        ) : (
          <EmptyState title="No sellers found" description="Try changing the game or the rating range." />
        )}
      </FetchBoundary>
    </>
  );
}
