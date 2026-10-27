import { useCallback, useState } from 'react';
import { useParams } from 'react-router-dom';
import { commentsApi } from '../api/comments';
import { sellersApi } from '../api/sellers';
import Alert from '../components/Alert';
import Avatar from '../components/Avatar';
import CommentForm from '../components/CommentForm';
import CommentItem from '../components/CommentItem';
import EmptyState from '../components/EmptyState';
import FetchBoundary from '../components/FetchBoundary';
import RatingBadge from '../components/RatingBadge';
import { ANONYMOUS_AUTHOR } from '../constants/comments';
import { useAuth } from '../context/AuthContext';
import { useFetch } from '../hooks/useFetch';
import { useSubmit } from '../hooks/useSubmit';
import { formatDate, getFullName } from '../utils/format';

export default function SellerPage() {
  const { id } = useParams();
  const { isAuthenticated } = useAuth();
  const [notice, setNotice] = useState('');
  const [actionError, setActionError] = useState(null);

  const fetchSeller = useCallback((signal) => sellersApi.getById(id, signal), [id]);
  const fetchComments = useCallback((signal) => commentsApi.list(id, signal), [id]);

  const seller = useFetch(fetchSeller);
  const comments = useFetch(fetchComments);

  const createComment = useCallback(
    async (payload) => {
      const response = await commentsApi.create(id, payload);
      setNotice(response);
      return response;
    },
    [id],
  );

  const { submit: submitComment, pending, error: commentError } = useSubmit(createComment);

  const runAction = async (action) => {
    setActionError(null);
    try {
      const response = await action();
      setNotice(response);
      comments.reload();
    } catch (error) {
      setActionError(error);
    }
  };

  const fullName = getFullName(seller.data);

  return (
    <FetchBoundary loading={seller.loading} error={seller.error}>
      {seller.data && (
        <>
          <section className="profile">
            <Avatar name={fullName} size="lg" />
            <div className="profile__info">
              <h1 className="profile__name">{fullName}</h1>
              <p className="muted">Member since {formatDate(seller.data.registrationDate)}</p>
            </div>
            <RatingBadge value={seller.data.rating} size="lg" />
          </section>

          <Alert variant="success">{notice}</Alert>
          <Alert>{actionError?.message}</Alert>

          <div className="columns">
            <section>
              <h2 className="section-title">Reviews</h2>
              <FetchBoundary loading={comments.loading} error={comments.error}>
                {comments.data?.length ? (
                  <div className="list">
                    {comments.data.map((comment) => (
                      <CommentItem
                        key={comment.id}
                        comment={comment}
                        canManage={isAuthenticated && comment.authorName !== ANONYMOUS_AUTHOR}
                        onUpdate={(commentId, payload) =>
                          runAction(() => commentsApi.update(id, commentId, payload))
                        }
                        onDelete={(commentId) => runAction(() => commentsApi.remove(id, commentId))}
                      />
                    ))}
                  </div>
                ) : (
                  <EmptyState title="No reviews yet" description="Be the first to share your experience." />
                )}
              </FetchBoundary>
            </section>

            <aside>
              <CommentForm onSubmit={submitComment} pending={pending} error={commentError} />
            </aside>
          </div>
        </>
      )}
    </FetchBoundary>
  );
}
