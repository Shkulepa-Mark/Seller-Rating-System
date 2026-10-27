import { useCallback, useState } from 'react';
import { adminApi } from '../api/admin';
import Alert from '../components/Alert';
import Avatar from '../components/Avatar';
import EmptyState from '../components/EmptyState';
import FetchBoundary from '../components/FetchBoundary';
import PageHeader from '../components/PageHeader';
import RatingBadge from '../components/RatingBadge';
import { useFetch } from '../hooks/useFetch';
import { formatDate, getFullName } from '../utils/format';

const TABS = {
  sellers: 'Sellers',
  comments: 'Comments',
};

function ReviewActions({ onReview }) {
  return (
    <div className="review-actions">
      <button type="button" className="btn btn--small" onClick={() => onReview(true)}>
        Approve
      </button>
      <button type="button" className="btn btn--small btn--danger" onClick={() => onReview(false)}>
        Decline
      </button>
    </div>
  );
}

function PendingSellers({ onError }) {
  const fetchSellers = useCallback((signal) => adminApi.getPendingSellers(signal), []);
  const { data, loading, error, reload } = useFetch(fetchSellers);

  const handleReview = async (id, approved) => {
    try {
      await adminApi.reviewSeller(id, approved);
      reload();
    } catch (caught) {
      onError(caught);
    }
  };

  return (
    <FetchBoundary loading={loading} error={error}>
      {data?.length ? (
        <div className="list">
          {data.map((seller) => (
            <article key={seller.id} className="card card--row">
              <Avatar name={getFullName(seller)} />
              <div className="card__grow">
                <h3 className="card__title">{getFullName(seller)}</h3>
                <p className="muted">
                  {seller.email} · requested {formatDate(seller.registrationDate)}
                </p>
              </div>
              <ReviewActions onReview={(approved) => handleReview(seller.id, approved)} />
            </article>
          ))}
        </div>
      ) : (
        <EmptyState title="No pending sellers" description="New registration requests will show up here." />
      )}
    </FetchBoundary>
  );
}

function PendingComments({ onError }) {
  const fetchComments = useCallback((signal) => adminApi.getPendingComments(signal), []);
  const { data, loading, error, reload } = useFetch(fetchComments);

  const handleReview = async (id, approved) => {
    try {
      await adminApi.reviewComment(id, approved);
      reload();
    } catch (caught) {
      onError(caught);
    }
  };

  return (
    <FetchBoundary loading={loading} error={error}>
      {data?.length ? (
        <div className="list">
          {data.map((comment) => (
            <article key={comment.id} className="card card--row">
              <div className="card__grow">
                <p>{comment.message}</p>
                <p className="muted">
                  {comment.authorName} · {formatDate(comment.createdAt)}
                </p>
              </div>
              <RatingBadge value={comment.rating} size="sm" />
              <ReviewActions onReview={(approved) => handleReview(comment.id, approved)} />
            </article>
          ))}
        </div>
      ) : (
        <EmptyState title="No pending comments" description="Everything has been reviewed." />
      )}
    </FetchBoundary>
  );
}

export default function AdminPage() {
  const [activeTab, setActiveTab] = useState('sellers');
  const [actionError, setActionError] = useState(null);

  const handleTabChange = (tab) => {
    setActionError(null);
    setActiveTab(tab);
  };

  return (
    <>
      <PageHeader title="Moderation" subtitle="Review seller profiles and comments before they go public." />

      <div className="tabs" role="tablist">
        {Object.entries(TABS).map(([key, label]) => (
          <button
            key={key}
            type="button"
            role="tab"
            aria-selected={activeTab === key}
            className={activeTab === key ? 'tabs__item tabs__item--active' : 'tabs__item'}
            onClick={() => handleTabChange(key)}
          >
            {label}
          </button>
        ))}
      </div>

      <Alert>{actionError?.message}</Alert>

      {activeTab === 'sellers' ? (
        <PendingSellers onError={setActionError} />
      ) : (
        <PendingComments onError={setActionError} />
      )}
    </>
  );
}
